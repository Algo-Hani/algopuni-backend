package algohani.moduleuserapi.domain.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

import algohani.common.enums.TokenName;
import algohani.common.exception.CustomException;
import algohani.moduleuserapi.domain.auth.dto.request.RefreshReqDto;
import algohani.moduleuserapi.domain.auth.dto.response.TokenDto.AccessTokenDto;
import algohani.moduleuserapi.global.exception.ErrorCode;
import algohani.moduleuserapi.global.security.jwt.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.mock.web.MockHttpServletRequest;

@ExtendWith(MockitoExtension.class)
class TokenRefreshServiceTest {

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private TokenRefreshService tokenRefreshService;

    @Nested
    @DisplayName("Access Token 갱신")
    class Access_Token_갱신 {

        private final MockHttpServletRequest request = new MockHttpServletRequest();

        private final RefreshReqDto refreshReqDto = new RefreshReqDto("refreshToken");

        @Test
        @DisplayName("성공")
        void 성공() {
            // given
            AccessTokenDto accessTokenDto = AccessTokenDto.builder()
                .token("token")
                .build();

            given(redisTemplate.opsForValue()).willReturn(valueOperations);
            given(redisTemplate.opsForValue().get(any())).willReturn("id");
            given(jwtTokenProvider.generateAccessToken(any())).willReturn(accessTokenDto);

            // when
            AccessTokenDto result = tokenRefreshService.refresh(refreshReqDto);

            // then
            assertThat(result).isEqualTo(accessTokenDto);
            then(redisTemplate.opsForValue()).should().get(any());
            then(jwtTokenProvider).should().generateAccessToken(any());
        }

        @Test
        @DisplayName("실패 - 쿠키에 Refresh Token이 존재하지 않음")
        void 실패_Refresh_Token_존재하지_않음() {
            // given
            given(redisTemplate.opsForValue()).willReturn(valueOperations);

            // when
            assertThatThrownBy(() -> tokenRefreshService.refresh(refreshReqDto))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_REFRESH_TOKEN);

            // then
            then(redisTemplate.opsForValue()).should(never()).get(TokenName.USER_REFRESH_TOKEN.name());
            then(jwtTokenProvider).should(never()).generateAccessToken(any());
        }

        @Test
        @DisplayName("실패 - Redis에 Refresh Token이 존재하지 않음")
        void 실패_Redis에_Refresh_Token_존재하지_않음() {
            // given
            given(redisTemplate.opsForValue()).willReturn(valueOperations);
            given(redisTemplate.opsForValue().get(refreshReqDto.refreshToken())).willReturn(null);

            // when
            assertThatThrownBy(() -> tokenRefreshService.refresh(refreshReqDto))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_REFRESH_TOKEN);

            // then
            then(redisTemplate.opsForValue()).should().get(refreshReqDto.refreshToken());
            then(jwtTokenProvider).should(never()).generateAccessToken(any());
        }
    }
}