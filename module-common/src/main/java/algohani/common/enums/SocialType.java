package algohani.common.enums;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum SocialType {
    KAKAO,
    NAVER,
    GOOGLE,
    GITHUB;

    /**
     * SocialType 반환
     *
     * @param name 소셜 타입
     * @return SocialType
     */
    public static SocialType getSocialType(final String name) {
        return SocialType.valueOf(name.toUpperCase());
    }
}
