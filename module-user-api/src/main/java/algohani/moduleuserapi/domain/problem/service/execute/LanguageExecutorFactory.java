package algohani.moduleuserapi.domain.problem.service.execute;

import algohani.common.enums.LanguageType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LanguageExecutorFactory {

    private final JavaExecutor javaExecutor;

    private final JavaScriptExecutor javaScriptExecutor;

    private final PythonExecutor pythonExecutor;

    public LanguageExecutor getExecutor(LanguageType languageType) {
        return switch (languageType) {
            case JAVA -> javaExecutor;
            case JAVASCRIPT -> javaScriptExecutor;
            case PYTHON3 -> pythonExecutor;
            default -> throw new RuntimeException("Not supported language"); // TODO : 예외 처리
        };
    }
}
