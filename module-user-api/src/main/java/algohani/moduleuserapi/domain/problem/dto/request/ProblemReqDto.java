package algohani.moduleuserapi.domain.problem.dto.request;

import algohani.common.dto.BasePageRequestDto;
import algohani.common.enums.LanguageType;
import algohani.common.enums.YNFlag;
import algohani.moduleuserapi.domain.problem.enums.ProblemOrder;
import algohani.moduleuserapi.domain.problem.enums.ProblemStatus;
import java.util.List;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProblemReqDto {

    @Getter
    @ToString(callSuper = true)
    public static class Search extends BasePageRequestDto {

        private final String title;

        private final List<Integer> levels;

        private final List<LanguageType> languages;

        private final List<ProblemStatus> statuses;

        private final ProblemOrder order;

        private final YNFlag bookmarked;

        @Builder
        public Search(String title, Long page, List<Integer> levels, List<String> languages, List<String> statuses, String order, String bookmarked) {
            super(page, 20L);

            this.title = StringUtils.defaultString(title);
            this.levels = levels == null ? List.of() : levels;
            this.languages = CollectionUtils.isEmpty(languages) ? List.of() : languages.stream()
                .map(LanguageType::of)
                .filter(Objects::nonNull)
                .toList();
            this.statuses = CollectionUtils.isEmpty(statuses) ? List.of() : statuses.stream()
                .map(ProblemStatus::of)
                .filter(Objects::nonNull)
                .toList();
            this.order = ProblemOrder.of(order);
            this.bookmarked = YNFlag.of(bookmarked);
        }

        /**
         * 페이징 시작 인덱스 반환
         *
         * @return 페이징 시작 인덱스
         */
        public long getFirstIndex() {
            return (this.getPage() - 1) * this.getSize();
        }
    }
}
