package algohani.moduleuserapi.domain.problem.service.codegen.execute;

import algohani.common.entity.Problem;
import algohani.common.entity.TestCase;
import algohani.common.entity.TestCaseInput;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class JavaExecutionCodeGenerator implements ExecutionCodeGenerator {

    @Override
    public List<ExecutionCodeDto> generateCode(Problem problem, String userCode) {
        List<ExecutionCodeDto> executionCodeDtos = new ArrayList<>();
        Set<TestCase> testCases = problem.getTestCases();

        for (TestCase testCase : testCases) {
            final String mainCode = generateMainCodeStart() +
                generateTestCaseInputCode(testCase) +
                generateOutputComparisonCode(testCase) +
                "}'";

            // 사용자 입력 코드 치환
            userCode = sanitizeUserCode(userCode);

            executionCodeDtos.add(new ExecutionCodeDto(mainCode, userCode));
        }

        return executionCodeDtos;
    }


    /**
     * 메인 코드 시작 부분 생성
     */
    private String generateMainCodeStart() {
        return "'class Main { " +
            "public static void main(String[] args) { " +
            "Solution sol = new Solution();";
    }

    /**
     * 입력 처리 코드 생성
     */
    private String generateTestCaseInputCode(TestCase testCase) {
        StringBuilder sb = new StringBuilder("var result = sol.solution(");

        for (int i = 0; i < testCase.getTestCaseInputs().size(); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(generateInputCodeByType(testCase.getTestCaseInputs().get(i)));
        }
        sb.append(");");

        return sb.toString();
    }

    /**
     * 입력 타입에 따른 코드 생성
     */
    private String generateInputCodeByType(TestCaseInput testCaseInput) {
        return switch (testCaseInput.getInputType()) {
            case INT, DOUBLE, STRING -> testCaseInput.getInput();
            case INT_ARRAY -> "new int[]" + testCaseInput.getInput();
            case STRING_ARRAY -> "new String[]" + testCaseInput.getInput();
            case INT_2D_ARRAY -> "new int[][]" + testCaseInput.getInput();
        };
    }

    /**
     * 출력값 비교 코드 생성
     */
    private String generateOutputComparisonCode(TestCase testCase) {
        switch (testCase.getOutputType()) {
            case INT, DOUBLE -> {
                return generatePrimitiveComparison(testCase.getOutput());
            }
            case INT_ARRAY -> {
                return generateArrayComparison(testCase.getOutput(), "int");
            }
            case STRING -> {
                return generateStringComparison(testCase.getOutput());
            }
            case STRING_ARRAY -> {
                return generateArrayComparison(testCase.getOutput(), "String");
            }
            default -> {
                return ""; // TODO : 예외 처리
            }
        }
    }

    /**
     * int, double 타입 출력 비교
     */
    private String generatePrimitiveComparison(final String expectedOutput) {
        return "if(result == " + expectedOutput + ")" +
            " System.out.println(\"정답\");" +
            "else System.out.println(\"오답\");}";
    }

    /**
     * String 타입 출력 비교
     */
    private String generateStringComparison(final String expectedOutput) {
        return "if(result.equals(" + expectedOutput + "))" +
            " System.out.println(\"정답\");" +
            "else System.out.println(\"오답\");}";
    }

    /**
     * int[], String[] 타입 출력 비교
     */
    private String generateArrayComparison(final String expectedOutput, final String arrayType) {
        return "if(compareArrays(result, new " + arrayType + "[]" + expectedOutput + "))" +
            " System.out.println(\"정답\");" +
            "else System.out.println(\"오답\");}" +
            generateArrayComparisonMethod(arrayType);
    }

    /**
     * 배열 비교 메소드 생성
     */
    private String generateArrayComparisonMethod(final String arrayType) {
        return "private static boolean compareArrays(" + arrayType + "[] array1, " + arrayType + "[] array2) {" +
            "if (array1.length != array2.length) {" +
            "return false;" +
            "}" +
            "for (int i = 0; i < array1.length; i++) {" +
            "if (array1[i] != array2[i]) {" +
            "return false;" +
            "}" +
            "}" +
            "return true;" +
            "}";
    }

    /**
     * 사용자 코드 줄바꿈, 탭 문자 치환
     */
    private String sanitizeUserCode(String userCode) {
        return userCode.replace("\\n", "").replace("\\t", "");
    }
}
