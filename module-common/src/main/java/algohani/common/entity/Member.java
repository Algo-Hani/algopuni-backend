package algohani.common.entity;

import algohani.common.enums.SocialType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
    @Column(name = "id", nullable = false, length = 50)
    @Comment("아이디")
    private String id;

    @Column(name = "password", length = 200)
    @Comment("비밀번호")
    private String password;

    @Column(name = "nickname", nullable = false, length = 10)
    @Comment("닉네임")
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(name = "social_type")
    @Comment("SNS 로그인 유형")
    private SocialType socialType;

    @Builder
    public Member(String id, String password, String nickname, SocialType socialType) {
        this.id = id;
        this.password = password;
        this.nickname = nickname;
        this.socialType = socialType;
    }

    /**
     * 소셜 로그인 여부 확인
     *
     * @return 소셜 로그인 여부
     */
    public boolean isSocial() {
        return socialType != null;
    }
}
