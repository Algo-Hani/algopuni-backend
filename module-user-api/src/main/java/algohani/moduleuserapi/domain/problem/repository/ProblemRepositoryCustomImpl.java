package algohani.moduleuserapi.domain.problem.repository;

import static algohani.common.entity.QLanguage.language;
import static algohani.common.entity.QProblem.problem;

import algohani.common.dto.PageResponseDto;
import algohani.common.enums.YNFlag;
import algohani.moduleuserapi.domain.problem.dto.request.ProblemReqDto;
import algohani.moduleuserapi.domain.problem.dto.response.ProblemResDto;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

@RequiredArgsConstructor
public class ProblemRepositoryCustomImpl implements ProblemRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public PageResponseDto<ProblemResDto.Search> findProblemWithPaging(ProblemReqDto.Search search) {
        List<ProblemResDto.Search> result = getPagingResult(search);
        final long totalElements = getTotalElements(search);

        return PageResponseDto.of(search, totalElements, result);
    }

    /**
     * 문제 목록 페이징 결과 조회
     */
    private List<ProblemResDto.Search> getPagingResult(ProblemReqDto.Search search) {
        JPAQuery<ProblemResDto.Search> jpaQuery = queryFactory.select(Projections.constructor(
                    ProblemResDto.Search.class,
                    problem.problemId,
                    problem.title,
                    problem.level,
                    problem.level, // TODO : 완료한 사람으로 변경
                    problem.level // TODO : 정답률로 변경
                )
            )
            .from(problem)
            .innerJoin(problem.languages, language);

        // 언어 조건
        if (!CollectionUtils.isEmpty(search.getLanguages())) {
            jpaQuery.on(language.languageType.in(search.getLanguages()));
        }

        jpaQuery.where(getSearchConditions(search))
            .offset(search.getFirstIndex())
            .limit(search.getSize())
            .orderBy(problem.problemId.desc()) // TODO : 정렬 조건 추가(정답률순)
            .groupBy(problem.problemId)
            .fetch();

        return jpaQuery.fetch();
    }

    /**
     * 문제 목록 전체 데이터 수 조회
     */
    private long getTotalElements(ProblemReqDto.Search search) {
        JPAQuery<Long> jpaQuery = queryFactory
            .select(problem.countDistinct())
            .from(problem)
            .innerJoin(problem.languages, language);

        // 언어 조건
        if (!CollectionUtils.isEmpty(search.getLanguages())) {
            jpaQuery.on(language.languageType.in(search.getLanguages()));
        }
        jpaQuery.where(getSearchConditions(search));

        return Optional.ofNullable(jpaQuery.fetchOne())
            .orElse(0L);
    }

    /**
     * 문제 목록 검색 조건
     */
    private BooleanBuilder getSearchConditions(ProblemReqDto.Search search) {
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(problem.useFlag.eq(YNFlag.Y));

        // 검색어 조건
        if (StringUtils.isNotBlank(search.getTitle())) {
            builder.and(problem.title.contains(search.getTitle()));
        }

        // 난이도 조건
        if (!CollectionUtils.isEmpty(search.getLevels())) {
            builder.and(problem.level.in(search.getLevels()));
        }

        // TODO : 즐겨찾기한 문제 추가

        return builder;
    }
}
