package algohani.moduleuserapi.global.security.oauth.data;

import algohani.common.enums.SocialType;
import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class OAuth2UserInfoFactory {

    private final OAuth2UserInfo oAuth2UserInfo;

    public static OAuth2UserInfo of(SocialType socialType, Map<String, Object> attributes) {
        if (socialType == SocialType.KAKAO) {
            return new KakaoOAuth2UserInfo(attributes);
        } else if (socialType == SocialType.NAVER) {
            return new NaverOAuth2UserInfo(attributes);
        } else if (socialType == SocialType.GOOGLE) {
            return new GoogleOAuth2UserInfo(attributes);
        } else {
            return new GithubOAuth2UserInfo(attributes);
        }
    }
}
