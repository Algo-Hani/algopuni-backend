package algohani.moduleuserapi.domain.problem.dto.response;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProblemResDto {

    public record Search(
        Long problemId,
        String title,
        int level,
        int successCount,
        int successRate,
        boolean isFavorite
    ) {

    }
}
