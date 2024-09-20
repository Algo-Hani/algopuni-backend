package algohani.moduleuserapi.domain.auth.controller;

import algohani.common.dto.ApiResponse;
import algohani.moduleuserapi.domain.auth.dto.request.EmailCodeReqDto;
import algohani.moduleuserapi.domain.auth.dto.request.SignUpReqDto;
import algohani.moduleuserapi.domain.auth.service.SignUpService;
import algohani.moduleuserapi.global.dto.ResponseText;
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
}
