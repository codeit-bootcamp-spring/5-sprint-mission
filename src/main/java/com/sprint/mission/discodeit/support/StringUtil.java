package com.sprint.mission.discodeit.support;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Locale;
import java.util.regex.Pattern;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class StringUtil {
    public static boolean isNullOrBlank(String s) {
        return s == null || s.isBlank();
    }

    public static String stripToLowerCase(String s) {
        return (s == null) ? null : s.strip().toLowerCase();
    }

    public static String stripOrNull(String s) {
        return (s == null) ? null : s.strip();
    }

    public static String trimOrNull(String s) {
        return (s == null) ? null : s.trim();
    }

    public static String stripToEmpty(String s) {
        return (s == null) ? "" : s.strip();
    }

    public static String stripOrDefault(String s, String defaultValue) {
        return isNullOrBlank(s) ? defaultValue : s.strip();
    }

    public static String normalizeLower(String s) {
        if (s == null) return null;
        return s.strip().toLowerCase(Locale.ROOT);
    }

    public static String normalizeEmail(String email) {
        return normalizeLower(email);
    }

    public static String normalizeUsername(String username) {
        return normalizeLower(username);
    }

    private static final Pattern NON_DIGITS = Pattern.compile("\\D");

    public static String digitsOnlyOrNull(String s) {
        if (isNullOrBlank(s)) return null;
        String stripped = s.strip();
        if (stripped.isEmpty()) return null;
        String digits = NON_DIGITS.matcher(stripped).replaceAll("");
        return digits.isEmpty() ? null : digits;
    }
}
