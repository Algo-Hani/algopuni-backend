package algohani.common.enums;

import java.util.Arrays;

public enum LanguageType {
    JAVA,
    PYTHON2,
    PYTHON3,
    C,
    JAVASCRIPT;

    public static LanguageType of(String language) {
        return Arrays.stream(LanguageType.values())
            .filter(type -> type.name().equalsIgnoreCase(language))
            .findFirst()
            .orElse(null);
    }
}
