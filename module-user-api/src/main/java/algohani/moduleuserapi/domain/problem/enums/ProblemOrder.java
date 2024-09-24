package algohani.moduleuserapi.domain.problem.enums;

public enum ProblemOrder {
    ACCEPTANCE_DESC, // 정답률 오름차순
    ACCEPTANCE_ASC, // 정답률 내림차순
    RECENT; // 최신순

    public static ProblemOrder of(String order) {
        if (order == null) {
            return RECENT;
        }

        for (ProblemOrder problemOrder : values()) {
            if (problemOrder.name().equalsIgnoreCase(order)) {
                return problemOrder;
            }
        }

        return RECENT;
    }
}
