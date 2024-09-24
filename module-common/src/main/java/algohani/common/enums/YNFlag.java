package algohani.common.enums;

public enum YNFlag {
    Y,
    N;

    public static YNFlag of(String flag) {
        if (flag == null) {
            return null;
        }

        for (YNFlag ynFlag : values()) {
            if (ynFlag.name().equalsIgnoreCase(flag)) {
                return ynFlag;
            }
        }

        return null;
    }
}
