package algohani.moduleuserapi.domain.problem.service;

import algohani.common.entity.Problem;
import algohani.common.exception.CustomException;
import algohani.moduleuserapi.domain.problem.dto.request.ProblemReqDto;
import algohani.moduleuserapi.domain.problem.repository.ProblemRepository;
import algohani.moduleuserapi.domain.problem.service.codegen.execute.ExecutionCodeDto;
import algohani.moduleuserapi.domain.problem.service.codegen.execute.ExecutionCodeGenerator;
import algohani.moduleuserapi.domain.problem.service.codegen.execute.ExecutionCodeGeneratorFactory;
import algohani.moduleuserapi.domain.problem.service.execute.LanguageExecutor;
import algohani.moduleuserapi.domain.problem.service.execute.LanguageExecutorFactory;
import algohani.moduleuserapi.global.exception.ErrorCode;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.ExecCreateCmdResponse;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.api.model.StreamType;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProblemRunnerService {

    private final ProblemRepository problemRepository;

    private final DockerClient dockerClient;

    private final LanguageExecutorFactory languageExecutorFactory;

    public void run(ProblemReqDto.Run dto) throws InterruptedException {
        Problem problem = problemRepository.findProblemWithTestCases(dto.problemId())
            .orElseThrow(() -> new CustomException(ErrorCode.PROBLEM_NOT_FOUND));
        log.info("{}", problem.getTestCases());

        ExecutionCodeGenerator executionCodeGenerator = ExecutionCodeGeneratorFactory.getExecutionCodeGenerator(dto.languageType());
        List<ExecutionCodeDto> executionCodeDtos = executionCodeGenerator.generateCode(problem, dto.sourceCode());
        LanguageExecutor languageExecutor = languageExecutorFactory.getExecutor(dto.languageType());
        for (ExecutionCodeDto executionCodeDto : executionCodeDtos) {
            languageExecutor.execute(executionCodeDto.mainCode(), executionCodeDto.userCode());
        }
//        for (ExecutionCodeDto executionCodeDto : executionCodeDtos) {
//            if (dto.languageType() == LanguageType.JAVA) {
//                final String containerId = dockerClient.createContainerCmd("openjdk:17")
//                    .withName("java17-" + UUID.randomUUID())
//                    .withHostName("potatowoong")
//                    .exec()
//                    .getId();
//
//                dockerClient.startContainerCmd(containerId).exec();
//
//                String[] saveSourceCommand = {"sh", "-c", "echo " + executionCodeDto.mainCode() + " > Main.java && echo '" + executionCodeDto.userCode() + "' > Solution.java"};
//                String[] compileCommand = {"sh", "-c", "javac Main.java Solution.java"};
//                String[] runCommand = {"sh", "-c", "java Main"};
//
//                StringBuilder standardOutputLogs = new StringBuilder();
//                StringBuilder standardErrorLogs = new StringBuilder();
//
//                // 소스코드 생성
//                if (!executeCommand(containerId, saveSourceCommand, standardOutputLogs, standardErrorLogs)) {
//                    logErrorAndCleanup(containerId, "소스코드 생성 에러", standardErrorLogs);
//                    return;
//                }
//
//                // 컴파일
//                if (!executeCommand(containerId, compileCommand, standardOutputLogs, standardErrorLogs)) {
//                    logErrorAndCleanup(containerId, "컴파일 에러", standardErrorLogs);
//                    return;
//                }
//
//                // 실행
//                if (!executeCommand(containerId, runCommand, standardOutputLogs, standardErrorLogs)) {
//                    logErrorAndCleanup(containerId, "실행 에러", standardErrorLogs);
//                    return;
//                } else {
//                    log.info("[실행 결과] :: {}", standardOutputLogs);
//                }
//
//                cleanupContainer(containerId);
//            } else if (dto.languageType() == LanguageType.JAVASCRIPT) {
//                final String containerId = dockerClient.createContainerCmd("node:20")
//                    .withName("node-" + UUID.randomUUID())
//                    .withHostName("potatowoong")
//                    .withCmd("sh", "-c", "tail -f /dev/null") // 컨테이너 종료 방지
//                    .exec()
//                    .getId();
//
//                dockerClient.startContainerCmd(containerId).exec();
//
//                String[] saveSourceCommand = {"sh", "-c", "echo '" + executionCodeDto.userCode() + "' > app.js && echo '" + executionCodeDto.mainCode() + "' >> app.js"};
//                String[] runCommand = {"sh", "-c", "node app.js"};
//
//                StringBuilder standardOutputLogs = new StringBuilder();
//                StringBuilder standardErrorLogs = new StringBuilder();
//
//                // 소스코드 생성
//                if (!executeCommand(containerId, saveSourceCommand, standardOutputLogs, standardErrorLogs)) {
//                    logErrorAndCleanup(containerId, "소스코드 생성 에러", standardErrorLogs);
//                    return;
//                }
//
//                // 실행
//                if (!executeCommand(containerId, runCommand, standardOutputLogs, standardErrorLogs)) {
//                    logErrorAndCleanup(containerId, "실행 에러", standardErrorLogs);
//                    return;
//                } else {
//                    log.info("[실행 결과] :: {}", standardOutputLogs);
//                }
//
//                cleanupContainer(containerId);
//            } else if (dto.languageType() == LanguageType.PYTHON3) {
//                final String containerId = dockerClient.createContainerCmd("python:3")
//                    .withName("python3-" + UUID.randomUUID())
//                    .withHostName("potatowoong")
//                    .withCmd("sh", "-c", "tail -f /dev/null") // 컨테이너 종료 방지
//                    .exec()
//                    .getId();
//
//                dockerClient.startContainerCmd(containerId).exec();
//
//                String[] saveSourceCommand = {"sh", "-c", "echo '" + executionCodeDto.userCode() + "' > app.py && echo '" + executionCodeDto.mainCode() + "' >> app.py"};
//                String[] runCommand = {"sh", "-c", "python3 app.py"};
//
//                StringBuilder standardOutputLogs = new StringBuilder();
//                StringBuilder standardErrorLogs = new StringBuilder();
//
//                // 소스코드 생성
//                if (!executeCommand(containerId, saveSourceCommand, standardOutputLogs, standardErrorLogs)) {
//                    logErrorAndCleanup(containerId, "소스코드 생성 에러", standardErrorLogs);
//                    return;
//                }
//
//                // 실행
//                if (!executeCommand(containerId, runCommand, standardOutputLogs, standardErrorLogs)) {
//                    logErrorAndCleanup(containerId, "실행 에러", standardErrorLogs);
//                    return;
//                } else {
//                    log.info("[실행 결과] :: {}", standardOutputLogs);
//                }
//
//                cleanupContainer(containerId);
//            }
//        }

//        if (dto.languageType() == LanguageType.JAVA) {
//            Set<TestCase> testCases = problem.getTestCases();
//            for (TestCase testCase : testCases) {
//                StringBuilder mainCode = new StringBuilder();
//                mainCode.append("'class Main { ");
//                mainCode.append("public static void main(String[] args) { ");
//                mainCode.append("Solution sol = new Solution();");
//                mainCode.append("var result = sol.solution(");
//                for (int i = 0; i < testCase.getTestCaseInputs().size(); i++) {
//                    TestCaseInput testCaseInput = testCase.getTestCaseInputs().get(i);
//                    if (i > 0) {
//                        mainCode.append(", ");
//                    }
//                    ParameterType inputType = testCaseInput.getInputType();
//                    switch (inputType) {
//                        case INT, DOUBLE, STRING:
//                            mainCode.append(testCaseInput.getInput());
//                            break;
//                        case INT_ARRAY:
//                            mainCode.append("new int[]").append(testCaseInput.getInput());
//                            break;
//                        case STRING_ARRAY:
//                            mainCode.append("new String[]").append(testCaseInput.getInput());
//                            break;
//                        case INT_2D_ARRAY:
//                            mainCode.append("new int[][]").append(testCaseInput.getInput());
//                            break;
//                    }
//                }
//                mainCode.append(");");
//
//                // 출력값 비교
//                if (testCase.getOutputType() == ParameterType.INT || testCase.getOutputType() == ParameterType.DOUBLE) {
//                    mainCode.append("if(result == ");
//                    mainCode.append(testCase.getOutput()).append(")");
//                    mainCode.append(" System.out.println(\"정답\");");
//                    mainCode.append("else System.out.println(\"오답\");}");
//                } else if (testCase.getOutputType() == ParameterType.INT_ARRAY) {
//                    mainCode.append("if(compareIntArrays(result, new int[] ");
//                    mainCode.append(testCase.getOutput()).append("))");
//                    mainCode.append(" System.out.println(\"정답\");");
//                    mainCode.append("else System.out.println(\"오답\");}");
//
//                    // 배열 비교 메소드 추가
//                    mainCode.append("private static boolean compareIntArrays(int[] array1, int[] array2) {");
//                    mainCode.append("if (array1.length != array2.length) {");
//                    mainCode.append("return false;");
//                    mainCode.append("}");
//
//                    mainCode.append("for (int i = 0; i < array1.length; i++) {");
//                    mainCode.append("if (array1[i] != array2[i]) {");
//                    mainCode.append("return false;");
//                    mainCode.append("}");
//                    mainCode.append("}");
//
//                    mainCode.append("return true;}");
//                } else if (testCase.getOutputType() == ParameterType.STRING) {
//                    mainCode.append("if(result.equals(");
//                    mainCode.append(testCase.getOutput()).append("))");
//                    mainCode.append(" System.out.println(\"정답\");");
//                    mainCode.append("else System.out.println(\"오답\");}");
//                } else if (testCase.getOutputType() == ParameterType.STRING_ARRAY) {
//                    mainCode.append("if(compareStringArrays(result, new String[] ");
//                    mainCode.append(testCase.getOutput()).append("))");
//                    mainCode.append(" System.out.println(\"정답\");");
//                    mainCode.append("else System.out.println(\"오답\");}");
//
//                    // 배열 비교 메소드 추가
//                    mainCode.append("private static boolean compareStringArrays(String[] array1, String[] array2) {");
//                    mainCode.append("if (array1.length != array2.length) {");
//                    mainCode.append("return false;");
//                    mainCode.append("}");
//
//                    mainCode.append("for (int i = 0; i < array1.length; i++) {");
//                    mainCode.append("if (!array1[i].equals(array2[i])) {");
//                    mainCode.append("return false;");
//                    mainCode.append("}");
//                    mainCode.append("}");
//
//                    mainCode.append("return true;}");
//                }
//                mainCode.append("}'");
//
//                // 사용자 코드 입력
//                String userCode = dto.sourceCode();
//                userCode = userCode.replace("\\n", "").replace("\\t", "");
//
//                final String containerId = dockerClient.createContainerCmd("openjdk:17")
//                    .withName("java17-" + UUID.randomUUID())
//                    .withHostName("potatowoong")
//                    .exec()
//                    .getId();
//
//                dockerClient.startContainerCmd(containerId).exec();
//
//                String[] saveSourceCommand = {"sh", "-c", "echo " + mainCode + " > Main.java && echo '" + userCode + "' > Solution.java"};
//                String[] compileCommand = {"sh", "-c", "javac Main.java Solution.java"};
//                String[] runCommand = {"sh", "-c", "java Main"};
//
//                StringBuilder standardOutputLogs = new StringBuilder();
//                StringBuilder standardErrorLogs = new StringBuilder();
//
//                // 소스코드 생성
//                if (!executeCommand(containerId, saveSourceCommand, standardOutputLogs, standardErrorLogs)) {
//                    logErrorAndCleanup(containerId, "소스코드 생성 에러", standardErrorLogs);
//                    return;
//                }
//
//                // 컴파일
//                if (!executeCommand(containerId, compileCommand, standardOutputLogs, standardErrorLogs)) {
//                    logErrorAndCleanup(containerId, "컴파일 에러", standardErrorLogs);
//                    return;
//                }
//
//                // 실행
//                if (!executeCommand(containerId, runCommand, standardOutputLogs, standardErrorLogs)) {
//                    logErrorAndCleanup(containerId, "실행 에러", standardErrorLogs);
//                    return;
//                } else {
//                    log.info("[실행 결과] :: {}", standardOutputLogs);
//                }
//
//                cleanupContainer(containerId);
//            }
//        } else if (dto.languageType() == LanguageType.JAVASCRIPT) {
//            Set<TestCase> testCases = problem.getTestCases();
//            for (TestCase testCase : testCases) {
//                StringBuilder mainCode = new StringBuilder();
//                mainCode.append("function main() {");
//                mainCode.append("var result = solution(");
//
//                for (int i = 0; i < testCase.getTestCaseInputs().size(); i++) {
//                    TestCaseInput testCaseInput = testCase.getTestCaseInputs().get(i);
//                    if (i > 0) {
//                        mainCode.append(", ");
//                    }
//
//                    if (testCaseInput.getInputType() == ParameterType.INT_ARRAY || testCaseInput.getInputType() == ParameterType.STRING_ARRAY || testCaseInput.getInputType() == ParameterType.INT_2D_ARRAY) {
//                        mainCode.append(testCaseInput.getInput().replace("{", "[").replace("}", "]"));
//                    } else {
//                        mainCode.append(testCaseInput.getInput());
//                    }
//                }
//                mainCode.append(");");
//
//                // 출력값 비교
//                if (testCase.getOutputType() == ParameterType.STRING_ARRAY || testCase.getOutputType() == ParameterType.INT_ARRAY) {
//                    mainCode.append("if(JSON.stringify(result) === JSON.stringify(");
//                    mainCode.append(testCase.getOutput().replace("{", "[").replace("}", "]"));
//                    mainCode.append("))");
//                    mainCode.append(" console.log(\"정답\");");
//                    mainCode.append("else console.log(\"오답\");");
//                } else {
//                    mainCode.append("if(result === ");
//                    mainCode.append(testCase.getOutput().replace("{", "[").replace("}", "]")).append(")");
//                    mainCode.append(" console.log(\"정답\");");
//                    mainCode.append("else console.log(\"오답\");");
//                }
//
//                mainCode.append("};");
//                mainCode.append("main();");
//
//                // 사용자 코드 입력
//                String userCode = dto.sourceCode();
//                userCode = userCode.replace("\\n", "").replace("\\t", "");
//
//                final String containerId = dockerClient.createContainerCmd("node:20")
//                    .withName("node-" + UUID.randomUUID())
//                    .withHostName("potatowoong")
//                    .withCmd("sh", "-c", "tail -f /dev/null") // 컨테이너 종료 방지
//                    .exec()
//                    .getId();
//
//                dockerClient.startContainerCmd(containerId).exec();
//
//                String[] saveSourceCommand = {"sh", "-c", "echo '" + userCode + "' > app.js && echo '" + mainCode + "' >> app.js"};
//                String[] runCommand = {"sh", "-c", "node app.js"};
//
//                StringBuilder standardOutputLogs = new StringBuilder();
//                StringBuilder standardErrorLogs = new StringBuilder();
//
//                // 소스코드 생성
//                if (!executeCommand(containerId, saveSourceCommand, standardOutputLogs, standardErrorLogs)) {
//                    logErrorAndCleanup(containerId, "소스코드 생성 에러", standardErrorLogs);
//                    return;
//                }
//
//                // 실행
//                if (!executeCommand(containerId, runCommand, standardOutputLogs, standardErrorLogs)) {
//                    logErrorAndCleanup(containerId, "실행 에러", standardErrorLogs);
//                    return;
//                } else {
//                    log.info("[실행 결과] :: {}", standardOutputLogs);
//                }
//
//                cleanupContainer(containerId);
//            }
//        } else if (dto.languageType() == LanguageType.PYTHON3) {
//            Set<TestCase> testCases = problem.getTestCases();
//            for (TestCase testCase : testCases) {
//                StringBuilder mainCode = new StringBuilder();
//                mainCode.append("def main(): \n");
//                mainCode.append("\t\tresult = solution(");
//
//                for (int i = 0; i < testCase.getTestCaseInputs().size(); i++) {
//                    TestCaseInput testCaseInput = testCase.getTestCaseInputs().get(i);
//                    if (i > 0) {
//                        mainCode.append(", ");
//                    }
//
//                    if (testCaseInput.getInputType() == ParameterType.INT_ARRAY || testCaseInput.getInputType() == ParameterType.STRING_ARRAY || testCaseInput.getInputType() == ParameterType.INT_2D_ARRAY) {
//                        mainCode.append(testCaseInput.getInput().replace("{", "[").replace("}", "]"));
//                    } else {
//                        mainCode.append(testCaseInput.getInput());
//                    }
//                }
//                mainCode.append(")");
//
//                // 출력값 비교
//                if (testCase.getOutputType() == ParameterType.STRING_ARRAY || testCase.getOutputType() == ParameterType.INT_ARRAY) {
//                    mainCode.append("\n\t\tif result == ");
//                    mainCode.append(testCase.getOutput().replace("{", "[").replace("}", "]"));
//                    mainCode.append(" :\n");
//                    mainCode.append("\t\t\tprint(\"정답\")\n");
//                    mainCode.append("\t\telse: print(\"오답\")");
//                } else {
//                    mainCode.append("\n\t\tif result == ");
//                    mainCode.append(testCase.getOutput().replace("{", "[").replace("}", "]"));
//                    mainCode.append(" :\n");
//                    mainCode.append("\t\t\tprint(\"정답\")\n");
//                    mainCode.append("\t\telse: print(\"오답\")");
//                }
//
////                mainCode.append("};");
//                mainCode.append("\nmain()");
//
//                // 사용자 코드 입력
//                String userCode = "# -*- coding: utf-8 -*-\n";
//                userCode += dto.sourceCode();
////                String userCode = dto.sourceCode();
//                userCode = userCode.replace("\\n", "").replace("\\t", "");
//
//                final String containerId = dockerClient.createContainerCmd("python:3")
//                    .withName("python2-" + UUID.randomUUID())
//                    .withHostName("potatowoong")
//                    .withCmd("sh", "-c", "tail -f /dev/null") // 컨테이너 종료 방지
//                    .exec()
//                    .getId();
//
//                dockerClient.startContainerCmd(containerId).exec();
//
//                String[] saveSourceCommand = {"sh", "-c", "echo '" + userCode + "' > app.py && echo '" + mainCode + "' >> app.py"};
//                String[] runCommand = {"sh", "-c", "python3 app.py"};
//
//                log.info("user : {}", userCode);
//                log.info("main : {}", mainCode);
//
//                StringBuilder standardOutputLogs = new StringBuilder();
//                StringBuilder standardErrorLogs = new StringBuilder();
//
//                // 소스코드 생성
//                if (!executeCommand(containerId, saveSourceCommand, standardOutputLogs, standardErrorLogs)) {
//                    logErrorAndCleanup(containerId, "소스코드 생성 에러", standardErrorLogs);
//                    return;
//                }
//
//                // 실행
//                if (!executeCommand(containerId, runCommand, standardOutputLogs, standardErrorLogs)) {
//                    logErrorAndCleanup(containerId, "실행 에러", standardErrorLogs);
//                    return;
//                } else {
//                    log.info("[실행 결과] :: {}", standardOutputLogs);
//                }
//
//                cleanupContainer(containerId);
//            }
//        }
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
