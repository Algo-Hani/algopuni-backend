package algohani.moduleuserapi.domain.problem.service.execute;

public interface LanguageExecutor {

    void execute(final String mainCode, final String userCode) throws InterruptedException;
}
