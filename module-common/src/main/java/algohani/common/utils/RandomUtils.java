package algohani.common.utils;

import java.security.SecureRandom;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 랜덤 문자열 생성 유틸리티
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RandomUtils {

    private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private static final String LOWER = UPPER.toLowerCase();

    private static final String DIGITS = "0123456789";

    private static final String ALL = UPPER + LOWER + DIGITS;

    /**
     * 랜덤 문자열 생성(대문자, 소문자, 숫자)
     *
     * @param length 길이
     * @return 랜덤 문자열
     */
    public static String generateRandomString(final int length) {
        SecureRandom random = new SecureRandom();
        StringBuilder result = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(ALL.length());
            result.append(ALL.charAt(index));
        }
        return result.toString();
    }
}
