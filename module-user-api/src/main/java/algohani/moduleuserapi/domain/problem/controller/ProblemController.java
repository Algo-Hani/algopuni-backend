package algohani.moduleuserapi.domain.problem.controller;

import algohani.common.dto.ApiResponse;
import algohani.common.dto.PageResponseDto;
import algohani.moduleuserapi.domain.problem.dto.request.ProblemReqDto;
import algohani.moduleuserapi.domain.problem.dto.response.ProblemResDto.Search;
import algohani.moduleuserapi.domain.problem.service.ProblemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/problems")
@RequiredArgsConstructor
public class ProblemController {

    private final ProblemService problemService;

    /**
     * 문제 목록 조회 API
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponseDto<Search>>> search(ProblemReqDto.Search search) {
        return ApiResponse.success(problemService.getProblemsWithPaging(search));
    }
}
