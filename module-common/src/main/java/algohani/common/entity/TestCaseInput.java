package algohani.common.entity;

import algohani.common.enums.ParameterType;
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
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Comment("테스트 케이스 입력값 정보")
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE test_case_input SET del_flag = 'Y' WHERE test_case_input_id = ?")
@SQLRestriction(value = "del_flag = 'N'")
public class TestCaseInput extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "test_case_input_id", nullable = false)
    @Comment("테스트 케이스 입력값 정보 IDX")
    private Long testCaseInputId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_case_id", nullable = false, updatable = false)
    @Comment("테스트 케이스 정보 IDX")
    private TestCase testCase;

    @Column(name = "input", nullable = false, length = 100)
    @Comment("입력값")
    private String input;

    @Column(name = "input_type", nullable = false)
    @Comment("입력값 타입")
    @Enumerated(value = EnumType.STRING)
    private ParameterType inputType;
}
