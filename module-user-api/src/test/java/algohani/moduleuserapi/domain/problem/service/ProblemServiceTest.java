package algohani.moduleuserapi.domain.problem.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;

import algohani.common.dto.PageResponseDto;
import algohani.common.entity.FavoriteProblem;
import algohani.common.entity.Problem;
import algohani.common.enums.LanguageType;
import algohani.common.exception.CustomException;
import algohani.moduleuserapi.domain.auth.repository.MemberRepository;
import algohani.moduleuserapi.domain.problem.dto.response.ProblemResDto;
import algohani.moduleuserapi.domain.problem.dto.response.ProblemResDto.Search;
import algohani.moduleuserapi.domain.problem.repository.FavoriteProblemRepository;
import algohani.moduleuserapi.domain.problem.repository.ProblemRepository;
import algohani.moduleuserapi.global.exception.ErrorCode;
import algohani.moduleuserapi.global.utils.SecurityUtils;
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProblemServiceTest {

    @Mock
    private ProblemRepository problemRepository;

    @Mock
    private FavoriteProblemRepository favoriteProblemRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private ProblemService problemService;

    @Nested
    @DisplayName("목록 조회")
    class 목록_조회 {

        @Test
        @DisplayName("성공")
        void 성공() {
            // given
            ProblemResDto.Search search = new Search(1L, "title", 1, 1, 1, true);

            given(problemRepository.findProblemWithPaging(any())).willReturn(new PageResponseDto<>(10, 10, 10, 10, Collections.singletonList(search)));

            // when
            PageResponseDto<ProblemResDto.Search> result = problemService.getProblemsWithPaging(any());

            // then
            assertThat(result).isNotNull();

            then(problemRepository).should().findProblemWithPaging(any());
        }
    }

    @Nested
    @DisplayName("문제 즐겨찾기 추가")
    class 문제_즐겨찾기_추가 {

        private final long problemId = 1L;

        private final String userId = "userId";

        private final Problem problem = Problem.builder().build();

        private final FavoriteProblem favoriteProblem = FavoriteProblem.builder().build();

        @Test
        @DisplayName("성공 - 이미 즐겨찾기한 문제가 아닌 경우")
        void 즐겨찾기_성공_이미_즐겨찾기한_문제가_아닌_경우() {
            // given
            given(memberRepository.getReferenceById(userId)).willReturn(any());
            given(problemRepository.findUsingById(problemId)).willReturn(Optional.of(problem));
            given(favoriteProblemRepository.findByMemberIdAndProblemProblemId(userId, problemId)).willReturn(Optional.empty());

            // when
            try (MockedStatic<SecurityUtils> util = mockStatic(SecurityUtils.class)) {
                util.when(SecurityUtils::getCurrentUserId).thenReturn(userId);
                problemService.addFavoriteProblem(problemId);
            }

            // then
            then(memberRepository).should().getReferenceById(userId);
            then(problemRepository).should().findUsingById(problemId);
            then(favoriteProblemRepository).should().findByMemberIdAndProblemProblemId(userId, problemId);
            then(favoriteProblemRepository).should().save(any());
        }

        @Test
        @DisplayName("성공 - 이미 즐겨찾기한 문제인 경우")
        void 즐겨찾기_성공_이미_즐겨찾기한_문제인_경우() {
            // given
            given(memberRepository.getReferenceById(userId)).willReturn(any());
            given(problemRepository.findUsingById(problemId)).willReturn(Optional.of(problem));
            given(favoriteProblemRepository.findByMemberIdAndProblemProblemId(userId, problemId)).willReturn(Optional.of(favoriteProblem));

            // when
            try (MockedStatic<SecurityUtils> util = mockStatic(SecurityUtils.class)) {
                util.when(SecurityUtils::getCurrentUserId).thenReturn(userId);
                problemService.addFavoriteProblem(problemId);
            }

            // then
            then(memberRepository).should().getReferenceById(userId);
            then(problemRepository).should().findUsingById(problemId);
            then(favoriteProblemRepository).should().findByMemberIdAndProblemProblemId(any(), any());
            then(favoriteProblemRepository).should(never()).save(any());
        }

        @Test
        @DisplayName("실패 - 문제가 존재하지 않음")
        void 실패_문제가_존재하지_않음() {
            // given
            given(memberRepository.getReferenceById(userId)).willReturn(any());
            given(problemRepository.findUsingById(problemId)).willReturn(Optional.empty());

            // when & then
            try (MockedStatic<SecurityUtils> util = mockStatic(SecurityUtils.class)) {
                util.when(SecurityUtils::getCurrentUserId).thenReturn(userId);
                assertThatThrownBy(() -> problemService.addFavoriteProblem(problemId))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.PROBLEM_NOT_FOUND);
            }

            then(memberRepository).should().getReferenceById(userId);
            then(problemRepository).should().findUsingById(problemId);
            then(favoriteProblemRepository).should(never()).findByMemberIdAndProblemProblemId(any(), any());
            then(favoriteProblemRepository).should(never()).save(any());
        }
    }

    @Nested
    @DisplayName("문제 즐겨찾기 삭제")
    class 문제_즐겨찾기_삭제 {

        private final long problemId = 1L;

        private final String userId = "userId";

        private final FavoriteProblem favoriteProblem = FavoriteProblem.builder().build();

        @Test
        @DisplayName("성공 - 즐겨찾기한 문제인 경우")
        void 즐겨찾기_성공_즐겨찾기한_문제인_경우() {
            // given
            given(problemRepository.existsById(problemId)).willReturn(true);
            given(favoriteProblemRepository.findByMemberIdAndProblemProblemId(userId, problemId)).willReturn(Optional.of(favoriteProblem));

            // when
            try (MockedStatic<SecurityUtils> util = mockStatic(SecurityUtils.class)) {
                util.when(SecurityUtils::getCurrentUserId).thenReturn(userId);
                problemService.removeFavoriteProblem(problemId);
            }

            // then
            then(problemRepository).should().existsById(problemId);
            then(favoriteProblemRepository).should().findByMemberIdAndProblemProblemId(userId, problemId);
            then(favoriteProblemRepository).should().delete(any());
        }

        @Test
        @DisplayName("성공 - 즐겨찾기한 문제가 아닌 경우")
        void 즐겨찾기_성공_즐겨찾기한_문제가_아닌_경우() {
            // given
            given(problemRepository.existsById(problemId)).willReturn(true);
            given(favoriteProblemRepository.findByMemberIdAndProblemProblemId(userId, problemId)).willReturn(Optional.empty());

            // when
            try (MockedStatic<SecurityUtils> util = mockStatic(SecurityUtils.class)) {
                util.when(SecurityUtils::getCurrentUserId).thenReturn(userId);
                problemService.removeFavoriteProblem(problemId);
            }

            // then
            then(problemRepository).should().existsById(problemId);
            then(favoriteProblemRepository).should().findByMemberIdAndProblemProblemId(userId, problemId);
            then(favoriteProblemRepository).should(never()).delete(any());
        }

        @Test
        @DisplayName("실패 - 문제가 존재하지 않는 경우")
        void 실패_문제가_존재하지_않는_경우() {
            // given
            given(problemRepository.existsById(problemId)).willReturn(false);

            // when & then
            try (MockedStatic<SecurityUtils> util = mockStatic(SecurityUtils.class)) {
                util.when(SecurityUtils::getCurrentUserId).thenReturn(userId);
                assertThatThrownBy(() -> problemService.removeFavoriteProblem(problemId))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.PROBLEM_NOT_FOUND);
            }

            then(problemRepository).should().existsById(problemId);
            then(favoriteProblemRepository).should(never()).findByMemberIdAndProblemProblemId(any(), any());
            then(favoriteProblemRepository).should(never()).delete(any());
        }
    }

    @Nested
    @DisplayName("문제 상세 조회")
    class 문제_상세_조회 {

        private final long problemId = 1L;

        private final ProblemResDto.RelatedInfo relatedInfo = new ProblemResDto.RelatedInfo("title", "description", "restriction", "ioExample", "ioDescription", Collections.singletonList(LanguageType.JAVA), true);

        @Test
        @DisplayName("성공")
        void 성공() {
            // given
            final String language = "java";

            given(problemRepository.findProblemWithRelatedInfo(problemId)).willReturn(Optional.of(relatedInfo));

            // when
            ProblemResDto.RelatedInfo result = problemService.getProblem(problemId, language);

            // then
            assertThat(result).isNotNull();

            then(problemRepository).should().findProblemWithRelatedInfo(problemId);
        }

        @Test
        @DisplayName("실패 - 문제가 존재하지 않는 경우")
        void 실패_문제가_존재하지_않는_경우() {
            // given
            final String language = "java";

            given(problemRepository.findProblemWithRelatedInfo(problemId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> problemService.getProblem(problemId, language))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.PROBLEM_NOT_FOUND);

            then(problemRepository).should().findProblemWithRelatedInfo(problemId);
        }

        @Test
        @DisplayName("실패 - 지원하지 않는 언어인 경우")
        void 실패_지원하지_않는_언어인_경우() {
            // given
            final String language = "python";

            given(problemRepository.findProblemWithRelatedInfo(problemId)).willReturn(Optional.of(relatedInfo));

            // when & then
            assertThatThrownBy(() -> problemService.getProblem(problemId, language))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.LANGUAGE_NOT_SUPPORTED);

            then(problemRepository).should().findProblemWithRelatedInfo(problemId);
        }
    }
}