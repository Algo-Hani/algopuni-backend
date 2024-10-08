package algohani.moduleuserapi.global.dto;

import algohani.common.dto.BaseResponseText;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum ResponseText implements BaseResponseText {

    // 회원가입 관련 응답 메시지
    SIGN_UP_SUCCESS(HttpStatus.CREATED, "회원가입에 성공했습니다."),
    VERIFICATION_CODE_SENT(HttpStatus.OK, "인증번호가 전송되었습니다."),
    VERIFICATION_CODE_VERIFIED(HttpStatus.OK, "인증번호가 일치합니다."),

    // 로그인 관련 응답 메시지
    LOGIN_SUCCESS(HttpStatus.OK, "로그인에 성공했습니다."),
    LOGOUT_SUCCESS(HttpStatus.OK, "로그아웃에 성공했습니다."),

    // 토큰 관련 응답 메시지
    ACCESS_TOKEN_REFRESHED(HttpStatus.OK, "Access Token이 갱신되었습니다."),

    // 문제 관련 응답 메시지
    ADD_FAVORITE_SUCCESS(HttpStatus.OK, "즐겨찾기 추가가 완료되었습니다."),
    REMOVE_FAVORITE_SUCCESS(HttpStatus.OK, "즐겨찾기 해제가 완료되었습니다.");

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
