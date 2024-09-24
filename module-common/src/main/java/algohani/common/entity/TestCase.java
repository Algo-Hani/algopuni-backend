package algohani.common.entity;

import algohani.common.enums.ParameterType;
import algohani.common.enums.YNFlag;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Comment("테스트 케이스 정보")
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE test_case SET del_flag = 'Y' WHERE test_case_id = ?")
@SQLRestriction(value = "del_flag = 'N'")
public class TestCase extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "test_case_id", nullable = false)
    @Comment("테스트 케이스 정보 IDX")
    private Long testCaseId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id", nullable = false, updatable = false)
    @Comment("문제 정보 IDX")
    private Problem problem;

    @Column(name = "output", nullable = false, length = 100)
    @Comment("출력값")
    private String output;

    @Column(name = "output_type", nullable = false)
    @Comment("출력값 타입")
    @Enumerated(value = EnumType.STRING)
    private ParameterType outputType;

    @Column(name = "default_flag", nullable = false)
    @Comment("기본 테스트 케이스 여부")
    @Enumerated(value = EnumType.STRING)
    @ColumnDefault("'N'")
    private YNFlag defaultFlag;
}
