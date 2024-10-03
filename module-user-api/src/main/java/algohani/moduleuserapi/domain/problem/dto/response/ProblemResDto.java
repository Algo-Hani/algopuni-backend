package algohani.moduleuserapi.domain.problem.dto.response;

import algohani.common.enums.LanguageType;
import java.util.List;
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

    public record RelatedInfo(
        String title,
        String description,
        String restriction,
        String ioExample,
        String ioDescription,
        List<LanguageType> languageTypes,
        boolean isFavorite
    ) {

    }
}
