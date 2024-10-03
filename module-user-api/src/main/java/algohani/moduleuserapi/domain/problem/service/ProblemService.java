package algohani.moduleuserapi.domain.problem.service;

import algohani.common.dto.PageResponseDto;
import algohani.common.entity.FavoriteProblem;
import algohani.common.entity.Member;
import algohani.common.entity.Problem;
import algohani.common.enums.LanguageType;
import algohani.common.exception.CustomException;
import algohani.moduleuserapi.domain.auth.repository.MemberRepository;
import algohani.moduleuserapi.domain.problem.dto.request.ProblemReqDto;
import algohani.moduleuserapi.domain.problem.dto.response.ProblemResDto;
import algohani.moduleuserapi.domain.problem.repository.FavoriteProblemRepository;
import algohani.moduleuserapi.domain.problem.repository.ProblemRepository;
import algohani.moduleuserapi.global.exception.ErrorCode;
import algohani.moduleuserapi.global.utils.SecurityUtils;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProblemService {

    private final ProblemRepository problemRepository;

    private final FavoriteProblemRepository favoriteProblemRepository;

    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public PageResponseDto<ProblemResDto.Search> getProblemsWithPaging(ProblemReqDto.Search search) {
        return problemRepository.findProblemWithPaging(search);
    }

    @Transactional
    public void addFavoriteProblem(final long problemId) {
        final String userId = SecurityUtils.getCurrentUserId();

        // 사용자 정보 조회
        Member member = memberRepository.getReferenceById(userId);

        // 문제 정보 조회
        Problem problem = problemRepository.findUsingById(problemId)
            .orElseThrow(() -> new CustomException(ErrorCode.PROBLEM_NOT_FOUND));

        // 즐겨찾기 등록
        Optional<FavoriteProblem> savedFavoriteProblem = favoriteProblemRepository.findByMemberIdAndProblemProblemId(userId, problemId);
        if (savedFavoriteProblem.isEmpty()) {
            FavoriteProblem favoriteProblem = FavoriteProblem.builder()
                .member(member)
                .problem(problem)
                .build();
            favoriteProblemRepository.save(favoriteProblem);
        }
    }

    @Transactional
    public void removeFavoriteProblem(final long problemId) {
        final String userId = SecurityUtils.getCurrentUserId();

        // 문제 정보 조회
        if (!problemRepository.existsById(problemId)) {
            throw new CustomException(ErrorCode.PROBLEM_NOT_FOUND);
        }

        // 즐겨찾기 해제
        favoriteProblemRepository.findByMemberIdAndProblemProblemId(userId, problemId)
            .ifPresent(favoriteProblemRepository::delete);
    }

    @Transactional(readOnly = true)
    public ProblemResDto.RelatedInfo getProblem(final long problemId, final String language) {
        ProblemResDto.RelatedInfo relatedInfo = problemRepository.findProblemWithRelatedInfo(problemId)
            .orElseThrow(() -> new CustomException(ErrorCode.PROBLEM_NOT_FOUND));

        // 언어 지원 여부 확인
        checkLanguageSupport(language, relatedInfo);

        return relatedInfo;
    }

    /**
     * 언어 지원 여부 확인
     *
     * @param language    언어
     * @param relatedInfo 문제 상세 정보
     */
    private void checkLanguageSupport(final String language, final ProblemResDto.RelatedInfo relatedInfo) {
        if (StringUtils.isNotBlank(language)) {
            LanguageType languageType = LanguageType.of(language);

            // 지원하지 않는 언어인 경우
            if (!relatedInfo.languageTypes().contains(languageType)) {
                throw new CustomException(ErrorCode.LANGUAGE_NOT_SUPPORTED);
            }
        }
    }
}
