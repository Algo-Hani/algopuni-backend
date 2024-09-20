package algohani.moduleuserapi.global.exception;

import algohani.common.dto.BaseErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum ErrorCode implements BaseErrorCode {

    // 회원가입 관련 에러코드
    EMAIL_DUPLICATION(HttpStatus.BAD_REQUEST, "AU001", "이미 사용중인 이메일입니다."),
    NICKNAME_DUPLICATION(HttpStatus.BAD_REQUEST, "AU002", "이미 사용중인 닉네임입니다."),
    FAILED_TO_SEND_EMAIL(HttpStatus.INTERNAL_SERVER_ERROR, "AU003", "인증번호 전송에 실패했습니다."),
    FAILED_TO_VERIFY_EMAIL(HttpStatus.BAD_REQUEST, "AU004", "인증번호가 일치하지 않습니다."),
    PASSWORD_MISMATCH(HttpStatus.BAD_REQUEST, "AU005", "비밀번호가 일치하지 않습니다."),

    // 공통 에러코드
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "CM001", "서버 내부 에러입니다.");

    private final HttpStatus httpStatus;

    private final String code;

    private final String message;


    @Override
    public HttpStatus getHttpStatus() {
        return this.httpStatus;
    }

    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    @Override
    public String getName() {
        return this.name();
    }
}
