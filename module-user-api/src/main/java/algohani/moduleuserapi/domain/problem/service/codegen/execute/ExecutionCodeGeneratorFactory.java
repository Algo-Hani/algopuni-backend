package algohani.moduleuserapi.domain.problem.service.codegen.execute;

import algohani.common.enums.LanguageType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExecutionCodeGeneratorFactory {

    public static ExecutionCodeGenerator getExecutionCodeGenerator(LanguageType languageType) {
        switch (languageType) {
            case JAVA -> {
                return new JavaExecutionCodeGenerator();
            }
            case JAVASCRIPT -> {
                return new JavaScriptExecutionCodeGenerator();
            }
            case PYTHON3 -> {
                return new PythonExecutionCodeGenerator();
            }
            default -> throw new RuntimeException("Not supported language");
        }
    }
}
