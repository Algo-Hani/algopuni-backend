package algohani.moduleuserapi.global.security.handler;

import algohani.common.exception.CustomException;
import algohani.moduleuserapi.global.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

/**
 * <h2>JwtAuthenticationEntryPoint</h2>
 *
 * <p> 401 에러를 처리하는 클래스입니다. </p>
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Qualifier("handlerExceptionResolver")
    private final HandlerExceptionResolver resolver;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) {
        CustomException customException = (CustomException) request.getAttribute("exception");
        if (customException == null) {
            customException = new CustomException(ErrorCode.UNAUTHORIZED);
        }

        resolver.resolveException(request, response, null, customException);
    }
}
