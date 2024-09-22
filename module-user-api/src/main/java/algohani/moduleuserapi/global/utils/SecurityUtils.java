package algohani.moduleuserapi.global.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SecurityUtils {

    private static final String ANONYMOUS_USER = "anonymousUser";

    /**
     * 현재 인증된 사용자의 ID 조회
     *
     * @return 현재 사용자의 ID
     */
    public static String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName().equals(ANONYMOUS_USER) ? "" : authentication.getName();
        }
        return "";
    }
}
