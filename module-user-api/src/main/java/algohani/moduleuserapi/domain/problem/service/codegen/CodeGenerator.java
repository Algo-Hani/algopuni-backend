package algohani.moduleuserapi.domain.problem.service.codegen;

import algohani.common.entity.Parameter;
import algohani.common.enums.ParameterType;
import java.util.List;

public interface CodeGenerator {

    String generateCode(ParameterType returnType, List<Parameter> parameters);
}
