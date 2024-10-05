package algohani.moduleuserapi.domain.auth.controller;

import algohani.common.dto.ApiResponse;
import algohani.moduleuserapi.domain.auth.dto.request.EmailCodeReqDto;
import algohani.moduleuserapi.domain.auth.dto.request.LoginReqDto;
import algohani.moduleuserapi.domain.auth.dto.request.SignUpReqDto;
import algohani.moduleuserapi.domain.auth.dto.response.TokenDto;
import algohani.moduleuserapi.domain.auth.dto.response.TokenDto.AccessTokenDto;
import algohani.moduleuserapi.domain.auth.service.LoginService;
import algohani.moduleuserapi.domain.auth.service.LogoutService;
import algohani.moduleuserapi.domain.auth.service.SignUpService;
import algohani.moduleuserapi.domain.auth.service.TokenRefreshService;
import algohani.moduleuserapi.global.dto.ResponseText;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final SignUpService signUpService;

    private final LoginService loginService;

    private final LogoutService logoutService;

    private final TokenRefreshService tokenRefreshService;

    /**
     * 이메일 인증 코드 전송 API
     */
    @PostMapping("/email-verification/send-code")
    public ResponseEntity<ApiResponse<Void>> accessEmail(@RequestBody @Valid EmailCodeReqDto.Send send) {
        signUpService.sendEmailCode(send);

        return ApiResponse.success(ResponseText.VERIFICATION_CODE_SENT);
    }

    /**
     * 이메일 인증 코드 확인 API
     */
    @PostMapping("/email-verification/verify-code")
    public ResponseEntity<ApiResponse<Void>> verifyEmail(@RequestBody @Valid EmailCodeReqDto.Verify verify) {
        signUpService.verifyEmailCode(verify);

        return ApiResponse.success(ResponseText.VERIFICATION_CODE_VERIFIED);
    }

    /**
     * 회원가입 API
     */
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Void>> signup(@RequestBody @Valid SignUpReqDto dto) {
        signUpService.signup(dto);

        return ApiResponse.success(ResponseText.SIGN_UP_SUCCESS);
    }

    /**
     * 로그인 API
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenDto>> login(@Valid @RequestBody LoginReqDto loginReqDto) {
        TokenDto tokenDto = loginService.login(loginReqDto);

        return ApiResponse.success(ResponseText.LOGIN_SUCCESS, tokenDto);
    }

    /**
     * 로그아웃 API
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletResponse response) {
        logoutService.logout(response);

        return ApiResponse.success(ResponseText.LOGOUT_SUCCESS);
    }

    /**
     * Access Token 갱신 API
     */
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AccessTokenDto>> refresh(HttpServletRequest request) {
        AccessTokenDto accessTokenDto = tokenRefreshService.refresh(request);

        return ApiResponse.success(ResponseText.ACCESS_TOKEN_REFRESHED, accessTokenDto);
    }
}
