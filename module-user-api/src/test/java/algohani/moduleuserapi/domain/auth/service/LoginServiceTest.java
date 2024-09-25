package algohani.moduleuserapi.domain.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.mockStatic;

import algohani.common.entity.Member;
import algohani.common.enums.SocialType;
import algohani.common.enums.TokenName;
import algohani.common.exception.CustomException;
import algohani.common.utils.CookieUtils;
import algohani.moduleuserapi.domain.auth.dto.request.LoginReqDto;
import algohani.moduleuserapi.domain.auth.dto.response.TokenDto;
import algohani.moduleuserapi.domain.auth.dto.response.TokenDto.AccessTokenDto;
import algohani.moduleuserapi.domain.auth.dto.response.TokenDto.RefreshTokenDto;
import algohani.moduleuserapi.domain.auth.repository.MemberRepository;
import algohani.moduleuserapi.global.exception.ErrorCode;
import algohani.moduleuserapi.global.security.jwt.JwtTokenProvider;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class LoginServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private LoginService loginService;

    @Nested
    @DisplayName("로그인")
    class 로그인 {

        private final long tokenExpiresIn = LocalDateTime.now().plusDays(7).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        private final MockHttpServletResponse response = new MockHttpServletResponse();

        private final Member member = Member.builder()
            .id("id")
            .password("password")
            .build();

        private final LoginReqDto loginReqDto = LoginReqDto.builder()
            .id("id")
            .password("password")
            .build();

        private final AccessTokenDto accessTokenDto = AccessTokenDto.builder()
            .accessToken("accessToken")
            .expiresIn(tokenExpiresIn)
            .build();

        private final RefreshTokenDto refreshTokenDto = RefreshTokenDto.builder()
            .refreshToken("refreshToken")
            .expiresIn(tokenExpiresIn)
            .build();

        private final TokenDto tokenDto = TokenDto.builder()
            .accessToken(accessTokenDto)
            .refreshToken(refreshTokenDto)
            .build();

        @Test
        @DisplayName("성공")
        void 성공() {
            // given
            given(memberRepository.findById(any())).willReturn(Optional.of(member));
            given(passwordEncoder.matches(any(), any())).willReturn(true);
            given(jwtTokenProvider.generateToken(any())).willReturn(tokenDto);
            given(redisTemplate.opsForValue()).willReturn(valueOperations);
            willDoNothing().given(valueOperations).set(any(), any(), any(Long.class), any());

            // when
            try (MockedStatic<CookieUtils> util = mockStatic(CookieUtils.class)) {
                CookieUtils.createCookie(TokenName.USER_REFRESH_TOKEN.name(), refreshTokenDto.refreshToken(), refreshTokenDto.getExpiresInSecond(), new MockHttpServletResponse());

                AccessTokenDto result = loginService.login(loginReqDto, response);

                assertThat(result).isNotNull().isEqualTo(accessTokenDto);
                then(memberRepository).should().findById(any());
                then(passwordEncoder).should().matches(any(), any());
                then(jwtTokenProvider).should().generateToken(any());
                then(redisTemplate).should().opsForValue();
                then(valueOperations).should().set(any(), any(), any(Long.class), any());
            }

            // then

        }

        @Test
        @DisplayName("실패 - 아이디가 일치하지 않는 경우")
        void 실패_아이디가_일치하지_않는_경우() {
            // given
            given(memberRepository.findById(any())).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> loginService.login(loginReqDto, response))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.LOGIN_FAILED);

            then(memberRepository).should().findById(any());
            then(passwordEncoder).shouldHaveNoInteractions();
            then(jwtTokenProvider).shouldHaveNoInteractions();
            then(redisTemplate).shouldHaveNoInteractions();
            then(valueOperations).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("실패 - 비밀번호가 일치하지 않는 경우")
        void 실패_비밀번호가_일치하지_않는_경우() {
            // given
            given(memberRepository.findById(any())).willReturn(Optional.of(member));
            given(passwordEncoder.matches(any(), any())).willReturn(false);

            // when & then
            assertThatThrownBy(() -> loginService.login(loginReqDto, response))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.LOGIN_FAILED);

            then(memberRepository).should().findById(any());
            then(passwordEncoder).should().matches(any(), any());
            then(jwtTokenProvider).shouldHaveNoInteractions();
            then(redisTemplate).shouldHaveNoInteractions();
            then(valueOperations).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("실패 - 소셜 로그인 사용자인 경우")
        void 실패_소셜_로그인_사용자인_경우() {
            // given
            Member socialMember = Member.builder()
                .id("id")
                .password("password")
                .socialType(SocialType.KAKAO)
                .build();

            given(memberRepository.findById(any())).willReturn(Optional.of(socialMember));
            given(passwordEncoder.matches(any(), any())).willReturn(true);

            // when & then
            assertThatThrownBy(() -> loginService.login(loginReqDto, response))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.LOGIN_FAILED);

            then(memberRepository).should().findById(any());
            then(passwordEncoder).should().matches(any(), any());
            then(jwtTokenProvider).shouldHaveNoInteractions();
            then(redisTemplate).shouldHaveNoInteractions();
            then(valueOperations).shouldHaveNoInteractions();
        }
    }
}