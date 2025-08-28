package com.sprint.mission.discodeit.support;

import java.util.Locale;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class StringUtil {

    public static String nullOrStripAndLowerCase(String s) {
        return (s == null) ? null : s.strip().toLowerCase(Locale.ROOT);
    }

    public static String nullOrStrip(String s) {
        return (s == null) ? null : s.strip();
    }
}
