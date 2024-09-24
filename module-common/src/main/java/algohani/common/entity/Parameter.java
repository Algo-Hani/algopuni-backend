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
@Comment("파라미터 정보")
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE parameter SET del_flag = 'Y' WHERE parameter_id = ?")
@SQLRestriction(value = "del_flag = 'N'")
public class Parameter extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "parameter_id", nullable = false)
    @Comment("파라미터 정보 IDX")
    private Long parameterId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id", nullable = false, updatable = false)
    @Comment("문제 정보 IDX")
    private Problem problem;

    @Column(name = "parameterType", nullable = false)
    @Comment("파라미터 타입")
    @Enumerated(value = EnumType.STRING)
    private ParameterType parameterType;
}
