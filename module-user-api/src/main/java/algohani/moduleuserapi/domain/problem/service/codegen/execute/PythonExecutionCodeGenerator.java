package algohani.moduleuserapi.domain.problem.service.codegen.execute;

import algohani.common.entity.Problem;
import algohani.common.entity.TestCase;
import algohani.common.entity.TestCaseInput;
import algohani.common.enums.ParameterType;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class PythonExecutionCodeGenerator implements ExecutionCodeGenerator {

    @Override
    public List<ExecutionCodeDto> generateCode(Problem problem, String userCode) {
        List<ExecutionCodeDto> executionCodeDtos = new ArrayList<>();
        Set<TestCase> testCases = problem.getTestCases();

        for (TestCase testCase : testCases) {
            final String mainCode = generateMainCodeStart() +
                generateTestCaseInputCode(testCase) +
                generateOutputComparisonCode(testCase) +
                "\nmain()";

            // 사용자 입력 코드 치환
            String sanitizedUserCode = "# -*- coding: utf-8 -*-\n" + sanitizeUserCode(userCode);

            executionCodeDtos.add(new ExecutionCodeDto(mainCode, sanitizedUserCode));
        }

        return executionCodeDtos;
    }

    /**
     * 메인 코드 시작 부분 생성
     */
    private String generateMainCodeStart() {
        return "def main(): \n" +
            "\t\tresult = solution(";
    }

    /**
     * 입력 처리 코드 생성
     */
    private String generateTestCaseInputCode(TestCase testCase) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < testCase.getTestCaseInputs().size(); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(generateInputCodeByType(testCase.getTestCaseInputs().get(i)));
        }
        sb.append(")");

        return sb.toString();
    }

    /**
     * 입력 타입에 따른 코드 생성
     */
    private String generateInputCodeByType(TestCaseInput testCaseInput) {
        return switch (testCaseInput.getInputType()) {
            case INT_ARRAY, STRING_ARRAY, INT_2D_ARRAY -> testCaseInput.getInput().replace("{", "[").replace("}", "]");
            default -> testCaseInput.getInput();
        };
    }

    /**
     * 기본 타입 비교 코드 생성
     */
    private String generatePrimitiveComparison(final String expectedOutput) {
        return "\n\t\tif result == " + expectedOutput + " :\n" +
            "\t\t\tprint(\"정답\")\n" +
            "\t\telse: print(\"오답\")";
    }

    /**
     * 배열 타입 비교 코드 생성
     */
    private String generateArrayComparison(final String expectedOutput) {
        return "\n\t\tif result == " + expectedOutput.replace("{", "[").replace("}", "]") + " :\n" +
            "\t\t\tprint(\"정답\")\n" +
            "\t\telse: print(\"오답\")";
    }

    /**
     * 출력값 비교 코드 생성
     */
    private String generateOutputComparisonCode(TestCase testCase) {
        ParameterType outputType = testCase.getOutputType();
        if (outputType == ParameterType.INT_ARRAY || outputType == ParameterType.STRING_ARRAY) {
            return generateArrayComparison(testCase.getOutput());
        } else {
            return generatePrimitiveComparison(testCase.getOutput());
        }
    }

    /**
     * 사용자 코드 줄바꿈, 탭 문자 치환
     */
    private String sanitizeUserCode(String userCode) {
        return userCode.replace("\\n", "").replace("\\t", "");
    }
}
