package com.sprint.mission.discodeit.util;

public final class StringUtil {

    private StringUtil() {
        throw new AssertionError("Utility class");
    }

    public static boolean hasText(String str) {
        return (str != null && !str.isBlank());
    }

    public static String sanitizeFilename(String original) {
        if (original == null || original.isBlank()) {
            return "file";
        }

        String cleaned = original
            .replace("\r", "")
            .replace("\n", "")
            .replace("\t", "")
            .replace("/", "")
            .replace("\\", "")
            .replace("\"", "");

        return cleaned.isBlank() ? "file" : cleaned;
    }
}
