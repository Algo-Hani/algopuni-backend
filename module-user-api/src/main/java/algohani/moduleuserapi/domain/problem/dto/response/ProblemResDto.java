package algohani.moduleuserapi.domain.problem.dto.response;

import algohani.common.enums.LanguageType;
import algohani.common.enums.ParameterType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
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

    @Getter
    @AllArgsConstructor
    public static class RelatedInfo {

        private final String title;

        private final String description;

        private final String restriction;

        private final String ioExample;

        private final String ioDescription;

        @JsonIgnore
        private final ParameterType returnType;

        private final List<LanguageType> languageTypes;

        private final boolean favorite;

        private String sampleCode;

        public RelatedInfo(String title, String description, String restriction, String ioExample, String ioDescription, ParameterType returnType, List<LanguageType> languageTypes, boolean favorite) {
            this.title = title;
            this.description = description;
            this.restriction = restriction;
            this.ioExample = ioExample;
            this.ioDescription = ioDescription;
            this.returnType = returnType;
            this.languageTypes = languageTypes;
            this.favorite = favorite;
        }

        public void initSampleCode(final String sampleCode) {
            this.sampleCode = sampleCode;
        }
    }
}
