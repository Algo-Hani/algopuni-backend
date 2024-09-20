package algohani.moduleuserapi.global.exception;

import algohani.common.dto.ApiResponse;
import algohani.common.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class AlgopuniAdvice {

    /**
     * Handle CustomException
     */
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse<Void>> handleCustomException(CustomException e) {
        return ApiResponse.error(e.getErrorCode());
    }

    /**
     * Handle MethodArgumentNotValidException
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        return ApiResponse.fail(e.getBindingResult().getAllErrors().get(0).getDefaultMessage());
    }

    /**
     * Handle Exception
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        log.error("[Exception] :: ", e);
        return ApiResponse.error(ErrorCode.INTERNAL_SERVER_ERROR);
    }
}
