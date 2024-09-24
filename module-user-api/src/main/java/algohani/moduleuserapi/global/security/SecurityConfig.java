package algohani.moduleuserapi.global.security;

import algohani.moduleuserapi.global.filter.JwtAuthenticationFilter;
import algohani.moduleuserapi.global.security.handler.JwtAccessDeniedHandler;
import algohani.moduleuserapi.global.security.handler.JwtAuthenticationEntryPoint;
import algohani.moduleuserapi.global.security.handler.OAuth2LoginFailureHandler;
import algohani.moduleuserapi.global.security.handler.OAuth2LoginSuccessHandler;
import algohani.moduleuserapi.global.security.jwt.JwtTokenProvider;
import algohani.moduleuserapi.global.security.oauth.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    // 허용 URL
    private static final String[] PERMIT_ALL = {
        "/api/v1/auth/signup",
        "/api/v1/auth/email-verification/**",
        "/api/v1/auth/login",
        "/api/v1/auth/refresh",
        "/api/v1/problems",
        "/docs/swagger-ui/**",
        "/docs/openapi3.yaml",
        "/v3/api-docs/swagger-config",
        "/login/oauth2/code/**",
    };

    private final JwtTokenProvider jwtTokenProvider;

    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    private final CustomOAuth2UserService customOAuth2UserService;

    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;

    private final OAuth2LoginFailureHandler oAuth2LoginFailureHandler;

    /**
     * SecurityFilterChain 설정
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // Basic 인증 비활성화
        http.httpBasic(AbstractHttpConfigurer::disable);

        // CSRF 비활성화
        http.csrf(AbstractHttpConfigurer::disable);

        // FormLogin 비활성화
        http.formLogin(AbstractHttpConfigurer::disable);

        // 세션 비활성화
        http.sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // 인증 및 권한 설정
        http.authorizeHttpRequests(authorizeRequests -> authorizeRequests
            .requestMatchers(PERMIT_ALL).permitAll()
            .anyRequest().authenticated()
        );

        // 예외 처리 설정
        http.exceptionHandling(exceptionHandling -> exceptionHandling
            .authenticationEntryPoint(jwtAuthenticationEntryPoint)
            .accessDeniedHandler(jwtAccessDeniedHandler)
        );

        // OAuth2 로그인 설정
        http.oauth2Login(oauth2 -> oauth2
            .userInfoEndpoint(userInfoEndpoint -> userInfoEndpoint.userService(customOAuth2UserService))
            .successHandler(oAuth2LoginSuccessHandler)
            .failureHandler(oAuth2LoginFailureHandler)
        );

        // JwtAuthenticationFilter 추가
        http.addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * PasswordEncoder 설정
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
