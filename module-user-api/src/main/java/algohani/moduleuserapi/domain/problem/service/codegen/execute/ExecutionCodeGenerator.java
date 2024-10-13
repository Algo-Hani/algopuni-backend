package algohani.moduleuserapi.domain.problem.service.codegen.execute;

import algohani.common.entity.Problem;
import java.util.List;

public interface ExecutionCodeGenerator {

    List<ExecutionCodeDto> generateCode(Problem problem, String userCode);
}
