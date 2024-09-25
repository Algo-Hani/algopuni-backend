package algohani.common.utils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CookieUtils {

    /**
     * 쿠키 생성
     *
     * @param cookieName 쿠키명
     * @param value      쿠키값
     * @param maxAge     쿠키 만료 시간
     * @return 생성된 쿠키
     */
    public static Cookie createCookie(final String cookieName, final String value, final int maxAge) {
        final boolean isProd = isProd();
        Cookie cookie = new Cookie(cookieName, value);
        cookie.setHttpOnly(isProd);
        cookie.setSecure(isProd);
        cookie.setMaxAge(maxAge);
        cookie.setPath("/");

        return cookie;
    }

    /**
     * 쿠키의 값 조회
     *
     * @param cookies 쿠키 배열
     * @param name    쿠키명
     * @return 쿠키의 값
     */
    public static String getCookieValue(Cookie[] cookies, final String name) {
        if (cookies == null) {
            return null;
        }

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(name)) {
                return cookie.getValue();
            }
        }

        return null;
    }

    /**
     * 쿠키 삭제
     */
    public static void removeCookie(final String cookieName, final HttpServletResponse response) {
        final boolean isProd = isProd();
        Cookie cookie = new Cookie(cookieName, null);
        cookie.setHttpOnly(isProd);
        cookie.setSecure(isProd);
        cookie.setMaxAge(0);
        cookie.setPath("/");

        response.addCookie(cookie);
    }


    /**
     * 프로덕션 환경 여부 확인
     *
     * @return 프로덕션 환경 여부
     */
    private static boolean isProd() {
        HttpServletRequest servletRequest = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        final String origin = servletRequest.getHeader("Origin");
        return origin != null && origin.equals("https://www.algopuni.site");
    }
}
