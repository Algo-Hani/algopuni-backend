package algohani.moduleuserapi.domain.problem.enums;

public enum ProblemStatus {
    UNSOLVED, // 안 푼 문제
    SOLVING, // 풀고 있는 문제
    SOLVED_SELF, // 푼 문제 (스스로 해결)
    SOLVED_WITH_HELP; // 푼 문제 (다른 사람 풀이 확인)

    public static ProblemStatus of(String status) {
        for (ProblemStatus value : values()) {
            if (value.name().equals(status)) {
                return value;
            }
        }
        return null;
    }
}
