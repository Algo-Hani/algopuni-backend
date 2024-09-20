package algohani.common.dto;

import org.springframework.http.HttpStatus;

/**
 * <h2>BaseResponseText</h2>
 *
 * <p>API 응답 메시지를 정의하는 인터페이스입니다.</p>
 */
public interface BaseResponseText {
    
    HttpStatus getHttpStatus();

    String getMessage();
}
