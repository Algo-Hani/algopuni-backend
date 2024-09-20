package algohani.common.exception;

import algohani.common.dto.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * <h2>CustomException</h2>
 *
 * <p>사용자 정의 예외 클래스입니다.</p>
 */
@Getter
@RequiredArgsConstructor
public class CustomException extends RuntimeException {

    private final BaseErrorCode errorCode;
}
