package algohani.moduleuserapi.global.security.jwt;

import algohani.moduleuserapi.domain.auth.dto.response.TokenDto;
import algohani.moduleuserapi.domain.auth.dto.response.TokenDto.AccessTokenDto;
import algohani.moduleuserapi.domain.auth.dto.response.TokenDto.RefreshTokenDto;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Jwts.SIG;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import java.util.stream.Collectors;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.access-token-expiration}")
    private String accessTokenExpiration;

    @Value("${jwt.refresh-token-expiration}")
    private String refreshTokenExpiration;

    /**
     * Access Token과 Refresh Token을 생성하는 메소드
     */
    public TokenDto generateToken(Authentication authentication) {
        AccessTokenDto accessTokenDto = generateAccessToken(authentication);
        RefreshTokenDto refreshTokenDto = generateRefreshToken();

        return TokenDto.builder()
            .accessToken(accessTokenDto)
            .refreshToken(refreshTokenDto)
            .build();
    }

    /**
     * Access Token을 생성하는 메소드
     */
    private AccessTokenDto generateAccessToken(Authentication authentication) {
        String authorities = authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.joining(","));

        long now = (new Date()).getTime();

        // Access Token 만료 시간 설정
        Date accessTokenExpiresIn = new Date(now + 1000 * Long.parseLong(accessTokenExpiration));

        // Access Token 생성
        String accessToken = Jwts.builder()
            .subject(authentication.getName())
            .claim("auth", authorities)
            .expiration(accessTokenExpiresIn)
            .signWith(getSigningKey(), SIG.HS256)
            .compact();

        return AccessTokenDto.builder()
            .accessToken(accessToken)
            .expiresIn(accessTokenExpiresIn.getTime())
            .build();
    }

    /**
     * Refresh Token을 생성하는 메소드
     */
    private RefreshTokenDto generateRefreshToken() {
        long now = (new Date()).getTime();

        // Refresh Token 만료 시간 설정
        Date refreshTokenExpiresIn = new Date(now + 1000 * Long.parseLong(refreshTokenExpiration));

        // Refresh Token 생성
        String refreshToken = Jwts.builder()
            .expiration(refreshTokenExpiresIn)
            .signWith(getSigningKey(), SIG.HS256)
            .compact();

        return RefreshTokenDto.builder()
            .refreshToken(refreshToken)
            .expiresIn(refreshTokenExpiresIn.getTime())
            .build();
    }

    /**
     * SecretKey를 생성하는 메소드
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
