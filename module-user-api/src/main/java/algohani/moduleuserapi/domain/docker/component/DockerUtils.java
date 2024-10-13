package algohani.moduleuserapi.domain.docker.component;

import algohani.common.exception.CustomException;
import algohani.moduleuserapi.domain.docker.dto.DockerResultDto;
import algohani.moduleuserapi.domain.docker.enums.DockerImageType;
import algohani.moduleuserapi.global.exception.ErrorCode;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.command.ExecCreateCmdResponse;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.api.model.StreamType;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DockerUtils {

    private final DockerClient dockerClient;

    public String startDockerContainer(DockerImageType dockerImageType) {
        String containerId;
        try (CreateContainerCmd createContainerCmd = dockerClient.createContainerCmd(dockerImageType.getImageName())) {
            containerId = createContainerCmd
                .withName(dockerImageType.name() + "-" + UUID.randomUUID())
                .withHostName("potatowoong")
//            .withCmd("sh", "-c", "tail -f /dev/null") // 컨테이너 종료 방지
                .exec()
                .getId();
        }

        dockerClient.startContainerCmd(containerId).exec();
        return containerId;
    }

    public DockerResultDto executeCommand(final String containerId, String[] command) {
        // 명령어 실행을 위한 명령어 생성
        ExecCreateCmdResponse execResponse = dockerClient.execCreateCmd(containerId)
            .withAttachStdout(true)
            .withAttachStderr(true)
            .withCmd(command)
            .exec();

        StringBuilder stdout = new StringBuilder();
        StringBuilder stderr = new StringBuilder();
        try {
            dockerClient.execStartCmd(execResponse.getId())
                .exec(new ResultCallback.Adapter<>() {
                    @Override
                    public void onNext(Frame object) {
                        if (object.getStreamType().equals(StreamType.STDOUT)) {
                            stdout.append(new String(object.getPayload(), StandardCharsets.UTF_8));
                        } else if (object.getStreamType().equals(StreamType.STDERR)) {
                            stderr.append(new String(object.getPayload(), StandardCharsets.UTF_8));
                        }
                    }
                })
                .awaitCompletion(60, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.error("[문제 실행 오류] :: ", e);
            Thread.currentThread().interrupt();
            throw new CustomException(ErrorCode.EXECUTE_FAILED);
        }

        DockerResultDto dockerResultDto = new DockerResultDto(stdout.toString(), stderr.toString());
        if (dockerResultDto.isFailed()) {
            cleanupContainer(containerId);
        }
        return dockerResultDto;
    }

    public void logErrorAndCleanup(final String containerId, final String errorMessage, StringBuilder stderr) {
        log.error("[{}] :: {}", errorMessage, stderr);
        cleanupContainer(containerId);
    }

    public void cleanupContainer(final String containerId) {
        dockerClient.stopContainerCmd(containerId).exec();
        dockerClient.removeContainerCmd(containerId).exec();
    }
}
