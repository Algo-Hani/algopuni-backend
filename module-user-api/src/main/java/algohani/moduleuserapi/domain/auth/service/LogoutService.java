package algohani.moduleuserapi.domain.auth.service;

import algohani.common.enums.TokenName;
import algohani.common.utils.CookieUtils;
import algohani.moduleuserapi.global.utils.SecurityUtils;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogoutService {

    private final RedisTemplate<String, String> redisTemplate;

    public void logout(HttpServletResponse response) {
        // Refresh Token 쿠키 삭제
        CookieUtils.removeCookie(TokenName.USER_REFRESH_TOKEN.name(), response);

        // Redis에서 Refresh Token 삭제
        redisTemplate.delete(SecurityUtils.getCurrentUserId());
    }
}
