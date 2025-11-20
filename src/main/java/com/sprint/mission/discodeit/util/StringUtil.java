package com.sprint.mission.discodeit.util;

public class StringUtil {

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
