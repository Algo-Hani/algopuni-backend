package algohani.moduleuserapi.domain.auth.service;

import algohani.common.entity.Member;
import algohani.common.enums.Role;
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
import jakarta.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final MemberRepository memberRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtTokenProvider jwtTokenProvider;

    private final RedisTemplate<String, String> redisTemplate;

    @Transactional(readOnly = true)
    public AccessTokenDto login(LoginReqDto loginReqDto, HttpServletResponse response) {
        // ID로 Member 조회
        Member savedMember = memberRepository.findById(loginReqDto.id())
            .orElseThrow(() -> new CustomException(ErrorCode.LOGIN_FAILED));

        // 비밀번호 일치 여부 확인
        if (!passwordEncoder.matches(loginReqDto.password(), savedMember.getPassword())) {
            throw new CustomException(ErrorCode.LOGIN_FAILED);
        }

        // 소셜 로그인 여부 확인
        if (savedMember.isSocial()) {
            throw new CustomException(ErrorCode.LOGIN_FAILED);
        }

        // Authentication 객체 생성
        Authentication authentication = new UsernamePasswordAuthenticationToken(
            savedMember.getId(),
            savedMember.getPassword(),
            Collections.singletonList(Role.ROLE_USER::name)
        );

        // 인증 정보를 기반으로 JWT Token 생성
        TokenDto tokenDto = jwtTokenProvider.generateToken(authentication);

        // Refresh Token을 Redis에 저장
        RefreshTokenDto refreshTokenDto = tokenDto.refreshToken();
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(savedMember.getId(), refreshTokenDto.refreshToken(), refreshTokenDto.expiresIn(), TimeUnit.MILLISECONDS);

        // Refresh Token을 쿠키에 담아서 전달
        CookieUtils.createCookie(TokenName.USER_REFRESH_TOKEN.name(), refreshTokenDto.refreshToken(), refreshTokenDto.getExpiresInSecond(), response);

        return tokenDto.accessToken();
    }
}
