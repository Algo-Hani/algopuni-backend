package algohani.moduleuserapi.global.security.oauth.data;

import algohani.common.enums.SocialType;
import java.util.Map;

public class NaverOAuth2UserInfo extends OAuth2UserInfo {

    public NaverOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    @SuppressWarnings("unchecked")
    public String getId() {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");

        return SocialType.NAVER.name() + "::" + response.get("id");
    }
}
