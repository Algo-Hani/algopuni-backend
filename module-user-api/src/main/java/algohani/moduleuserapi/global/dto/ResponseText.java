package algohani.moduleuserapi.global.dto;

import algohani.common.dto.BaseResponseText;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum ResponseText implements BaseResponseText {

    // 회원가입 관련 응답 메시지
    SIGN_UP_SUCCESS(HttpStatus.CREATED, "회원가입에 성공했습니다."),
    VERIFICATION_CODE_SENT(HttpStatus.OK, "인증번호가 전송되었습니다."),
    VERIFICATION_CODE_VERIFIED(HttpStatus.OK, "인증번호가 일치합니다.");

    private final HttpStatus httpStatus;

    private final String message;

    @Override
    public HttpStatus getHttpStatus() {
        return this.httpStatus;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
