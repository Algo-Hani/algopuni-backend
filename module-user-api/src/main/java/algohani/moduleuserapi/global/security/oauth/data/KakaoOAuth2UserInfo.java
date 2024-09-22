package algohani.moduleuserapi.global.security.oauth.data;

import algohani.common.enums.SocialType;
import java.util.Map;

public class KakaoOAuth2UserInfo extends OAuth2UserInfo {

    public KakaoOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getId() {
        return SocialType.KAKAO.name() + "::" + attributes.get("id");
    }
}
