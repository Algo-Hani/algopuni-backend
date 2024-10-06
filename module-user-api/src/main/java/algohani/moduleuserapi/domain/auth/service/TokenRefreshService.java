package algohani.moduleuserapi.domain.auth.service;

import algohani.common.enums.Role;
import algohani.common.exception.CustomException;
import algohani.moduleuserapi.domain.auth.dto.request.RefreshReqDto;
import algohani.moduleuserapi.domain.auth.dto.response.TokenDto.AccessTokenDto;
import algohani.moduleuserapi.global.exception.ErrorCode;
import algohani.moduleuserapi.global.security.jwt.JwtTokenProvider;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenRefreshService {

    private final RedisTemplate<String, String> redisTemplate;

    private final JwtTokenProvider jwtTokenProvider;

    public AccessTokenDto refresh(RefreshReqDto refreshReqDto) {
        return refresh(refreshReqDto.refreshToken());
    }

    /**
     * Refresh Token으로 Access Token 갱신
     */
    private AccessTokenDto refresh(final String refreshToken) {
        if (StringUtils.isBlank(refreshToken)) {
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        // Redis에서 Refresh Token으로 ID 가져오기
        final String userName = redisTemplate.opsForValue().get(refreshToken);
        if (StringUtils.isBlank(userName)) {
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        // Authentication 객체 생성
        Authentication authentication = new UsernamePasswordAuthenticationToken(userName, "", List.of(Role.ROLE_USER::name));

        // 인증 정보를 기반으로 JWT Token 생성
        return jwtTokenProvider.generateAccessToken(authentication);
    }
}
