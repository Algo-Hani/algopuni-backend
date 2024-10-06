package algohani.moduleuserapi.domain.problem.service.codegen;

import algohani.common.enums.LanguageType;
import algohani.common.exception.CustomException;
import algohani.moduleuserapi.global.exception.ErrorCode;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CodeGeneratorFactory {

    public static CodeGenerator getCodeGenerator(LanguageType languageType) {
        switch (languageType) {
            case JAVA -> {
                return new JavaCodeGenerator();
            }
            case PYTHON2, PYTHON3 -> {
                return new PythonCodeGenerator();
            }
            case JAVASCRIPT -> {
                return new JavaScriptCodeGenerator();
            }
            default -> throw new CustomException(ErrorCode.LANGUAGE_NOT_SUPPORTED);
        }
    }
}
