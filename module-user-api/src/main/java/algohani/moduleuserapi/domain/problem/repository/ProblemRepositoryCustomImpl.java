package algohani.moduleuserapi.domain.problem.repository;

import static algohani.common.entity.QFavoriteProblem.favoriteProblem;
import static algohani.common.entity.QLanguage.language;
import static algohani.common.entity.QProblem.problem;

import algohani.common.dto.PageResponseDto;
import algohani.common.entity.Problem;
import algohani.common.enums.LanguageType;
import algohani.common.enums.YNFlag;
import algohani.moduleuserapi.domain.problem.dto.request.ProblemReqDto;
import algohani.moduleuserapi.domain.problem.dto.response.ProblemResDto;
import algohani.moduleuserapi.global.utils.SecurityUtils;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
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

    @Override
    public Optional<Problem> findUsingById(final long problemId) {
        return Optional.ofNullable(
            queryFactory.selectFrom(problem)
                .where(problem.problemId.eq(problemId)
                    .and(problem.useFlag.eq(YNFlag.Y)))
                .fetchOne()
        );
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
                    problem.level, // TODO : 정답률로 변경
                    getIsFavoriteExpression()
                )
            )
            .from(problem)
            .innerJoin(problem.languages, language);

        // 언어 조건
        addLanguageCondition(jpaQuery, search.getLanguages());

        jpaQuery.where(getSearchConditions(search))
            .offset(search.getFirstIndex())
            .limit(search.getSize())
            .orderBy(problem.problemId.desc()) // TODO : 정렬 조건 추가(정답률순)
            .groupBy(problem.problemId)
            .fetch();

        return jpaQuery.fetch();
    }

    /**
     * 즐겨찾기 여부 서브쿼리
     *
     * @return 즐겨찾기 여부 서브쿼리
     */
    private BooleanExpression getIsFavoriteExpression() {
        final String userId = SecurityUtils.getCurrentUserId();

        return StringUtils.isBlank(userId) ? Expressions.asBoolean(false) : JPAExpressions.selectOne()
            .from(favoriteProblem)
            .where(favoriteProblem.member.id.eq(userId)
                .and(favoriteProblem.problem.problemId.eq(problem.problemId)))
            .exists();
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
        addLanguageCondition(jpaQuery, search.getLanguages());

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
        addTitleCondition(builder, search.getTitle());

        // 난이도 조건
        addLevelCondition(builder, search.getLevels());

        // 즐겨찾기 조건
        addFavoriteCondition(builder, search.getFavorite());

        return builder;
    }

    /**
     * 언어 조건 추가
     *
     * @param jpaQuery  JPAQuery
     * @param languages 언어 목록
     */
    private void addLanguageCondition(JPAQuery<?> jpaQuery, List<LanguageType> languages) {
        if (!CollectionUtils.isEmpty(languages)) {
            jpaQuery.on(language.languageType.in(languages));
        }
    }

    /**
     * 문제 제목 검색 조건 추가
     *
     * @param builder BooleanBuilder
     * @param title   제목
     */
    private void addTitleCondition(BooleanBuilder builder, String title) {
        if (StringUtils.isNotBlank(title)) {
            builder.and(problem.title.contains(title));
        }
    }

    /**
     * 문제 난이도 검색 조건 추가
     *
     * @param builder BooleanBuilder
     * @param levels  난이도
     */
    private void addLevelCondition(BooleanBuilder builder, List<Integer> levels) {
        if (!CollectionUtils.isEmpty(levels)) {
            builder.and(problem.level.in(levels));
        }
    }

    /**
     * 즐겨찾기 조건 추가
     *
     * @param builder  BooleanBuilder
     * @param favorite 즐겨찾기 여부
     */
    private void addFavoriteCondition(BooleanBuilder builder, YNFlag favorite) {
        final String userId = SecurityUtils.getCurrentUserId();
        if (StringUtils.isNotBlank(userId)) {
            BooleanBuilder favoriteBuilder = new BooleanBuilder();
            favoriteBuilder.and(favoriteProblem.member.id.eq(userId)
                .and(favoriteProblem.problem.problemId.eq(problem.problemId)));

            if (favorite == YNFlag.Y) {
                builder.and(JPAExpressions.selectOne()
                    .from(favoriteProblem)
                    .where(favoriteBuilder)
                    .exists());
            } else if (favorite == YNFlag.N) {
                builder.and(JPAExpressions.selectOne()
                    .from(favoriteProblem)
                    .where(favoriteBuilder)
                    .notExists());
            }
        }
    }
}
