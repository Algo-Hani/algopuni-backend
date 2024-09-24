package algohani.common.entity;

import algohani.common.enums.ParameterType;
import algohani.common.enums.YNFlag;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Comment("문제 정보")
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE problem SET del_flag = 'Y' WHERE problem_id = ?")
@SQLRestriction(value = "del_flag = 'N'")
public class Problem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "problem_id", nullable = false)
    @Comment("문제 정보 IDX")
    private Long problemId;

    @Column(name = "title", nullable = false, length = 30)
    @Comment("문제명")
    private String title;

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    @Comment("문제 설명")
    private String description;

    @Column(name = "restriction", columnDefinition = "TEXT")
    @Comment("제한 사항")
    private String restriction;

    @Column(name = "io_example", columnDefinition = "TEXT")
    @Comment("입출력 예시")
    private String ioExample;

    @Column(name = "io_description", columnDefinition = "TEXT")
    @Comment("입출력 예시 설명")
    private String ioDescription;

    @Column(name = "time_limit", nullable = false)
    @Comment("제한 시간")
    private int timeLimit;

    @Column(name = "memory_limit", nullable = false)
    @Comment("메모리 제한")
    private int memoryLimit;

    @Column(name = "level", nullable = false)
    @Comment("문제 Level")
    private int level;

    @Column(name = "return_type", nullable = false)
    @Comment("리턴 타입")
    @Enumerated(EnumType.STRING)
    private ParameterType returnType;

    @Column(name = "use_flag", nullable = false)
    @Comment("사용 여부")
    @Enumerated(EnumType.STRING)
    @ColumnDefault("'Y'")
    private YNFlag useFlag;

    @OneToMany(mappedBy = "problem")
    private List<Language> languages;

    @Builder
    public Problem(String title, String description, String restriction, String ioExample, String ioDescription, int timeLimit, int memoryLimit, int level, ParameterType returnType, YNFlag useFlag) {
        this.title = title;
        this.description = description;
        this.restriction = restriction;
        this.ioExample = ioExample;
        this.ioDescription = ioDescription;
        this.timeLimit = timeLimit;
        this.memoryLimit = memoryLimit;
        this.level = level;
        this.returnType = returnType;
        this.useFlag = useFlag;
    }
}
