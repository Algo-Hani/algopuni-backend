package algohani.moduleuserapi.global.security.handler;

import algohani.moduleuserapi.domain.auth.dto.response.TokenDto;
import algohani.moduleuserapi.domain.auth.dto.response.TokenDto.AccessTokenDto;
import algohani.moduleuserapi.domain.auth.dto.response.TokenDto.RefreshTokenDto;
import algohani.moduleuserapi.global.security.jwt.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

/**
 * <h2>OAuth2LoginSuccessHandler</h2>
 *
 * <p> OAuth2 로그인 성공 핸들러 클래스입니다. </p>
 */
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;

    private final RedisTemplate<String, String> redisTemplate;

    @Value("${spring.security.oauth2.redirect-url}")
    private String redirectUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        TokenDto tokenDto = jwtTokenProvider.generateToken(authentication);
        AccessTokenDto accessTokenDto = tokenDto.accessToken();
        RefreshTokenDto refreshTokenDto = tokenDto.refreshToken();

        // Refresh Token을 Redis에 저장
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(refreshTokenDto.token(), authentication.getName(), refreshTokenDto.getExpiresInSecond(), TimeUnit.SECONDS);

        response.sendRedirect(
            redirectUrl +
                "/oauth2-login-success?accessToken=" + accessTokenDto.token() +
                "&accessTokenExpiresIn=" + accessTokenDto.expiresIn() +
                "&refreshToken=" + refreshTokenDto.token() +
                "&refreshTokenExpiresIn=" + refreshTokenDto.expiresIn()
        );
    }
}
