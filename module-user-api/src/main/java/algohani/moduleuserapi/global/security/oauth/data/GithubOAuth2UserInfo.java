package algohani.moduleuserapi.global.security.oauth.data;

import algohani.common.enums.SocialType;
import java.util.Map;

public class GithubOAuth2UserInfo extends OAuth2UserInfo {

    public GithubOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getId() {
        return SocialType.GITHUB.name() + "::" + attributes.get("id");
    }
}
