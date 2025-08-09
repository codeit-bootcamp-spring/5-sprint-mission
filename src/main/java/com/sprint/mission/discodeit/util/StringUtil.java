package com.sprint.mission.discodeit.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class StringUtil {

    public static String normalizeString(String string) {
        return string == null ? "" : string.strip();
    }

    public static String extractDigits(String input) {
        if (input == null) return "";
        return input.replaceAll("\\D", "");
    }
}
