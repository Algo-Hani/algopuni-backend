package algohani.moduleuserapi.domain.problem.service.codegen;

import algohani.common.entity.Parameter;
import algohani.common.enums.ParameterType;
import java.util.List;

public class JavaCodeGenerator implements CodeGenerator {

    @Override
    public String generateCode(ParameterType returnType, List<Parameter> parameters) {
        StringBuilder sb = new StringBuilder();
        sb.append("class Solution {\n");
        sb.append("\tpublic ");
        sb.append(getReturnTypeString(returnType));
        sb.append(" solution(");

        for (int i = 0; i < parameters.size(); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            Parameter parameter = parameters.get(i);
            sb.append(getParameterString(parameter.getParameterType(), i + 1));
        }
        sb.append(") {\n");

        sb.append("\t\t").append(getAnswerInitialization(returnType));
        sb.append("\t\treturn answer;\n");
        sb.append("\t}\n");
        sb.append("}\n");

        return sb.toString();
    }

    private String getReturnTypeString(final ParameterType returnType) {
        return switch (returnType) {
            case INT_ARRAY -> "int[]";
            case STRING -> "String";
            case STRING_ARRAY -> "String[]";
            default -> returnType.name().toLowerCase();
        };
    }

    private String getParameterString(final ParameterType parameterType, final int index) {
        return switch (parameterType) {
            case INT_ARRAY -> "int[] input" + index;
            case STRING_ARRAY -> "String[] input" + index;
            case STRING -> "String input" + index;
            default -> parameterType.name().toLowerCase() + " input" + index;
        };
    }

    private String getAnswerInitialization(final ParameterType returnType) {
        return switch (returnType) {
            case INT -> "int answer = 0;\n";
            case STRING -> "String answer = \"\";\n";
            case STRING_ARRAY -> "String[] answer = new String[0];\n";
            case INT_ARRAY -> "int[] answer = new int[0];\n";
            case DOUBLE -> "double answer = 0.0;\n";
        };
    }
}
