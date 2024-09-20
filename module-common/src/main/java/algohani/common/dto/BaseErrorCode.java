package algohani.common.dto;

import org.springframework.http.HttpStatus;

/**
 * <h2>BaseErrorCode</h2>
 *
 * <p>API 에러코드를 정의하는 인터페이스입니다.</p>
 */
public interface BaseErrorCode {

    HttpStatus getHttpStatus();

    String getCode();

    String getMessage();

    String getName();
}
