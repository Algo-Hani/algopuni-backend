package algohani.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.time.LocalDateTime;
import lombok.Builder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * API 공통 응답 객체
 *
 * @param status     API 결과 상태
 * @param statusCode 상태 코드
 * @param message    메시지
 * @param data       데이터
 * @param errorCode  에러 코드
 * @param errorName  에러 이름
 * @param timestamp  응답 시간
 * @param <T>        데이터 타입
 */
@Builder
@JsonInclude(Include.NON_NULL)
public record ApiResponse<T>(
    Status status,
    int statusCode,
    String message,
    T data,
    String errorCode,
    String errorName,
    LocalDateTime timestamp
) {

    /**
     * 성공 응답
     *
     * @param data 응답 데이터
     * @param <T>  응답 데이터 타입
     * @return ResponseEntity
     */
    public static <T> ResponseEntity<ApiResponse<T>> success(final T data) {
        return ResponseEntity.ok(ApiResponse.<T>builder()
            .status(Status.SUCCESS)
            .statusCode(200)
            .data(data)
            .timestamp(LocalDateTime.now())
            .build()
        );
    }

    /**
     * 성공 응답
     *
     * @param baseResponseText 메시지
     * @return ResponseEntity
     */
    public static ResponseEntity<ApiResponse<Void>> success(final BaseResponseText baseResponseText) {
        return ResponseEntity.status(baseResponseText.getHttpStatus())
            .body(ApiResponse.<Void>builder()
                .status(Status.SUCCESS)
                .statusCode(baseResponseText.getHttpStatus().value())
                .message(baseResponseText.getMessage())
                .timestamp(LocalDateTime.now())
                .build()
            );
    }

    /**
     * 성공 응답
     *
     * @param baseResponseText 메시지
     * @param data             응답 데이터
     * @param <T>              응답 데이터 타입
     * @return ResponseEntity
     */
    public static <T> ResponseEntity<ApiResponse<T>> success(final BaseResponseText baseResponseText, final T data) {
        return ResponseEntity.status(baseResponseText.getHttpStatus())
            .body(ApiResponse.<T>builder()
                .status(Status.SUCCESS)
                .statusCode(baseResponseText.getHttpStatus().value())
                .message(baseResponseText.getMessage())
                .data(data)
                .timestamp(LocalDateTime.now())
                .build()
            );
    }

    /**
     * 실패 응답
     *
     * @param message 메시지
     * @return ResponseEntity
     */
    public static ResponseEntity<ApiResponse<Void>> fail(final String message) {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.<Void>builder()
                .status(Status.FAIL)
                .statusCode(400)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build()
            );
    }

    /**
     * 에러 응답
     *
     * @param errorCode 에러 코드
     * @return ResponseEntity
     */
    public static ResponseEntity<ApiResponse<Void>> error(final BaseErrorCode errorCode) {
        return ResponseEntity
            .status(errorCode.getHttpStatus())
            .body(ApiResponse.<Void>builder()
                .status(Status.ERROR)
                .statusCode(errorCode.getHttpStatus().value())
                .message(errorCode.getMessage())
                .errorName(errorCode.getName())
                .errorCode(errorCode.getCode())
                .timestamp(LocalDateTime.now())
                .build());
    }

    public enum Status {
        SUCCESS, FAIL, ERROR
    }
}
