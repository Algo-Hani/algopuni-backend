package algohani.moduleuserapi.domain.problem.service.execute;

import algohani.moduleuserapi.domain.docker.component.DockerUtils;
import algohani.moduleuserapi.domain.docker.enums.DockerImageType;
import com.github.dockerjava.api.DockerClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class JavaExecutor implements LanguageExecutor {

    private final DockerClient dockerClient;

    private final DockerUtils dockerUtils;

    @Override
    public void execute(String mainCode, String userCode) throws InterruptedException {
        String[] saveSourceCommand = {"sh", "-c", "echo " + mainCode + " > Main.java && echo '" + userCode + "' > Solution.java"};
        String[] compileCommand = {"sh", "-c", "javac Main.java Solution.java"};
        String[] runCommand = {"sh", "-c", "java Main"};

        StringBuilder standardOutputLogs = new StringBuilder();
        StringBuilder standardErrorLogs = new StringBuilder();

        final String containerId = dockerUtils.startDockerContainer(DockerImageType.JAVA);

        // 소스코드 생성
        if (!dockerUtils.executeCommand(containerId, saveSourceCommand, standardOutputLogs, standardErrorLogs)) {
            dockerUtils.logErrorAndCleanup(containerId, "소스코드 생성 에러", standardErrorLogs);
            return;
        }

        // 컴파일
        if (!dockerUtils.executeCommand(containerId, compileCommand, standardOutputLogs, standardErrorLogs)) {
            dockerUtils.logErrorAndCleanup(containerId, "컴파일 에러", standardErrorLogs);
            return;
        }

        // 실행
        if (!dockerUtils.executeCommand(containerId, runCommand, standardOutputLogs, standardErrorLogs)) {
            dockerUtils.logErrorAndCleanup(containerId, "실행 에러", standardErrorLogs);
            return;
        } else {
            log.info("[실행 결과] :: {}", standardOutputLogs);
        }

        dockerUtils.cleanupContainer(containerId);
    }
}
