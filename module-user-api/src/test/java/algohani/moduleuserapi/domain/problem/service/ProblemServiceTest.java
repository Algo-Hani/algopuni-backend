package algohani.moduleuserapi.domain.problem.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import algohani.common.dto.PageResponseDto;
import algohani.moduleuserapi.domain.problem.dto.response.ProblemResDto;
import algohani.moduleuserapi.domain.problem.dto.response.ProblemResDto.Search;
import algohani.moduleuserapi.domain.problem.repository.ProblemRepository;
import java.util.Collections;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProblemServiceTest {

    @Mock
    private ProblemRepository problemRepository;

    @InjectMocks
    private ProblemService problemService;

    @Nested
    @DisplayName("목록 조회")
    class 목록_조회 {

        @Test
        @DisplayName("성공")
        void 성공() {
            // given
            ProblemResDto.Search search = new Search(1L, "title", 1, 1, 1);

            given(problemRepository.findProblemWithPaging(any())).willReturn(new PageResponseDto<>(10, 10, 10, 10, Collections.singletonList(search)));

            // when
            PageResponseDto<ProblemResDto.Search> result = problemService.getProblemsWithPaging(any());

            // then
            assertThat(result).isNotNull();
            
            then(problemRepository).should().findProblemWithPaging(any());
        }
    }
}