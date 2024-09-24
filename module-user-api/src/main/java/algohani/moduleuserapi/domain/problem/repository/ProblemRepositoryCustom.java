package algohani.moduleuserapi.domain.problem.repository;

import algohani.common.dto.PageResponseDto;
import algohani.common.entity.Problem;
import algohani.moduleuserapi.domain.problem.dto.request.ProblemReqDto;
import algohani.moduleuserapi.domain.problem.dto.response.ProblemResDto.Search;
import java.util.Optional;

public interface ProblemRepositoryCustom {

    PageResponseDto<Search> findProblemWithPaging(ProblemReqDto.Search search);

    Optional<Problem> findUsingById(long problemId);
}
