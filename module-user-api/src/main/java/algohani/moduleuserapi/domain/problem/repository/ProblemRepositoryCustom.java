package algohani.moduleuserapi.domain.problem.repository;

import algohani.common.dto.PageResponseDto;
import algohani.moduleuserapi.domain.problem.dto.request.ProblemReqDto;
import algohani.moduleuserapi.domain.problem.dto.response.ProblemResDto.Search;

public interface ProblemRepositoryCustom {

    PageResponseDto<Search> findProblemWithPaging(ProblemReqDto.Search search);
}
