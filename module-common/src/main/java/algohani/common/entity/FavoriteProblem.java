package algohani.common.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Comment;

@Entity
@Comment("즐겨찾기한 문제 정보")
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FavoriteProblem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("즐겨찾기한 문제 정보 IDX")
    private Long favoriteProblemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id")
    @Comment("아이디")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id")
    @Comment("문제 정보 IDX")
    private Problem problem;

    @Builder
    public FavoriteProblem(Member member, Problem problem) {
        this.member = member;
        this.problem = problem;
    }
}
