package algohani.moduleuserapi.domain.problem.service;

import algohani.common.dto.PageResponseDto;
import algohani.moduleuserapi.domain.problem.dto.request.ProblemReqDto;
import algohani.moduleuserapi.domain.problem.dto.response.ProblemResDto;
import algohani.moduleuserapi.domain.problem.repository.ProblemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProblemService {

    private final ProblemRepository problemRepository;

    @Transactional(readOnly = true)
    public PageResponseDto<ProblemResDto.Search> getProblemsWithPaging(ProblemReqDto.Search search) {
        return problemRepository.findProblemWithPaging(search);
    }
}
