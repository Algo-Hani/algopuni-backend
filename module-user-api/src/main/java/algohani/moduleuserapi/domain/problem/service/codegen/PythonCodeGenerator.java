package algohani.moduleuserapi.domain.problem.service.codegen;

import algohani.common.entity.Parameter;
import algohani.common.enums.ParameterType;
import java.util.List;

public class PythonCodeGenerator implements CodeGenerator {

    @Override
    public String generateCode(ParameterType returnType, List<Parameter> parameters) {
        StringBuilder sb = new StringBuilder();

        sb.append("def solution(");
        for (int i = 0; i < parameters.size(); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append("input").append(i + 1);
        }
        sb.append("):\n");
        sb.append("\tanswer = ");
        sb.append(getAnswerInitialization(returnType)).append("\n");
        sb.append("\treturn answer\n");

        return sb.toString();
    }

    private String getAnswerInitialization(final ParameterType returnType) {
        return switch (returnType) {
            case INT -> "0";
            case STRING -> "\"\"";
            case STRING_ARRAY, INT_ARRAY -> "[]";
            case DOUBLE -> "0.0";
        };
    }
}
