package algohani.moduleuserapi.domain.problem.service.execute;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
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
public class JavaScriptExecutor implements LanguageExecutor {

    private final DockerClient dockerClient;

    @Override
    public void execute(String mainCode, String userCode) throws InterruptedException {
        final String containerId = dockerClient.createContainerCmd("node:20")
            .withName("node-" + UUID.randomUUID())
            .withHostName("potatowoong")
            .withCmd("sh", "-c", "tail -f /dev/null") // 컨테이너 종료 방지
            .exec()
            .getId();

        dockerClient.startContainerCmd(containerId).exec();

        String[] saveSourceCommand = {"sh", "-c", "echo '" + userCode + "' > app.js && echo '" + mainCode + "' >> app.js"};
        String[] runCommand = {"sh", "-c", "node app.js"};

        StringBuilder standardOutputLogs = new StringBuilder();
        StringBuilder standardErrorLogs = new StringBuilder();

        // 소스코드 생성
        if (!executeCommand(containerId, saveSourceCommand, standardOutputLogs, standardErrorLogs)) {
            logErrorAndCleanup(containerId, "소스코드 생성 에러", standardErrorLogs);
            return;
        }

        // 실행
        if (!executeCommand(containerId, runCommand, standardOutputLogs, standardErrorLogs)) {
            logErrorAndCleanup(containerId, "실행 에러", standardErrorLogs);
            return;
        } else {
            log.info("[실행 결과] :: {}", standardOutputLogs);
        }

        cleanupContainer(containerId);
    }

    private boolean executeCommand(final String containerId, String[] command, StringBuilder stdout, StringBuilder stderr) throws InterruptedException {
        ExecCreateCmdResponse execResponse = dockerClient.execCreateCmd(containerId)
            .withAttachStdout(true)
            .withAttachStderr(true)
            .withCmd(command)
            .exec();

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

        return stderr.isEmpty();
    }

    private void logErrorAndCleanup(final String containerId, final String errorMessage, StringBuilder stderr) {
        log.error("[{}] :: {}", errorMessage, stderr);
        cleanupContainer(containerId);
    }

    private void cleanupContainer(final String containerId) {
        dockerClient.stopContainerCmd(containerId).exec();
        dockerClient.removeContainerCmd(containerId).exec();
    }
}
