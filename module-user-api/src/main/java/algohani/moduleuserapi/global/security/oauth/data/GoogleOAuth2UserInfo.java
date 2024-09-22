package algohani.moduleuserapi.global.security.oauth.data;

import algohani.common.enums.SocialType;
import java.util.Map;

public class GoogleOAuth2UserInfo extends OAuth2UserInfo {

    public GoogleOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getId() {
        return SocialType.GOOGLE.name() + "::" + attributes.get("sub");
    }
}
