package algohani.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Comment;

@Entity
@Comment("사용자 정보")
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {

    @Id
    @Column(name = "id", nullable = false, length = 30)
    @Comment("아이디")
    private String id;

    @Column(name = "password", nullable = false, length = 200)
    @Comment("비밀번호")
    private String password;

    @Column(name = "nickname", nullable = false, length = 10)
    @Comment("닉네임")
    private String nickname;

    @Builder
    public Member(String id, String password, String nickname) {
        this.id = id;
        this.password = password;
        this.nickname = nickname;
    }
}
