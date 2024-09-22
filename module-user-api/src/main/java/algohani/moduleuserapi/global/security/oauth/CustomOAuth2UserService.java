package algohani.moduleuserapi.global.security.oauth;

import algohani.common.entity.Member;
import algohani.common.enums.SocialType;
import algohani.common.utils.RandomUtils;
import algohani.moduleuserapi.domain.auth.repository.MemberRepository;
import algohani.moduleuserapi.global.security.oauth.data.OAuth2UserInfo;
import algohani.moduleuserapi.global.security.oauth.data.OAuth2UserInfoFactory;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final MemberRepository memberRepository;

    @Override
    @Transactional
    @SuppressWarnings("unchecked")
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        try {
            OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
            OAuth2User oAuth2User = delegate.loadUser(userRequest);

            final String registrationId = userRequest.getClientRegistration().getRegistrationId();
            final String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUserNameAttributeName();
            Map<String, Object> attributes = oAuth2User.getAttributes();
            final SocialType socialType = SocialType.getSocialType(registrationId);

            OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.of(socialType, attributes);

            // 이미 가입된 회원인지 확인 후 가입되지 않은 회원이면 저장
            saveMember(oAuth2UserInfo, socialType);

            return new CustomOAuth2User(
                socialType == SocialType.NAVER ? (Map<String, Object>) oAuth2User.getAttributes().get("response") : oAuth2User.getAttributes(),
                socialType == SocialType.NAVER ? "id" : userNameAttributeName,
                oAuth2UserInfo.getId()
            );
        } catch (Exception e) {
            log.error("OAuth2 Login Failure: ", e);
            throw new OAuth2AuthenticationException(e.toString());
        }
    }

    /**
     * 이미 가입된 회원인지 확인 후 가입되지 않은 회원이면 저장
     */
    private void saveMember(OAuth2UserInfo oAuth2UserInfo, SocialType socialType) {
        Optional<Member> savedMember = memberRepository.findByIdAndSocialType(oAuth2UserInfo.getId(), socialType);

        if (savedMember.isEmpty()) {
            Member member = Member.builder()
                .id(oAuth2UserInfo.getId())
                .nickname("알고푸니" + RandomUtils.generateRandomNumberString(6))
                .socialType(socialType)
                .build();
            memberRepository.save(member);
        }
    }
}
