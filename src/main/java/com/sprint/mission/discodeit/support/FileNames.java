package com.sprint.mission.discodeit.support;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.MediaType;

import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FileNames {

    private static final SecureRandom RNG = new SecureRandom();

    private static final Map<String, String> EXT_BY_CT = Map.ofEntries(
            Map.entry("image/png", "png"),
            Map.entry("image/jpeg", "jpg"),
            Map.entry("image/gif", "gif"),
            Map.entry("image/webp", "webp"),
            Map.entry("image/svg+xml", "svg"),
            Map.entry("application/pdf", "pdf"),
            Map.entry("application/octet-stream", "bin")
    );

    public static String randomBase() {
        byte[] buf = new byte[16];
        RNG.nextBytes(buf);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(buf);
    }

    public static String extFromFilename(String originalFilename) {
        if (originalFilename == null || originalFilename.isBlank()) return null;
        String name = Paths.get(originalFilename).getFileName().toString().trim();
        int dot = name.lastIndexOf('.');
        if (dot < 0 || dot == name.length() - 1) return null;
        String ext = name.substring(dot + 1).toLowerCase(Locale.ROOT);
        return ext.matches("[a-z0-9]+") ? ext : null;
    }

    private static String baseNameFromFilename(String filename) {
        if (filename == null) return null;
        int dot = filename.lastIndexOf('.');
        if (dot > 0) {
            return filename.substring(0, dot);
        }
        return filename;
    }

    public static String extFromContentType(String contentType) {
        if (contentType == null || contentType.isBlank()) return null;
        try {
            MediaType mt = MediaType.parseMediaType(contentType);
            String key = (mt.getType() + "/" + mt.getSubtype()).toLowerCase(Locale.ROOT);
            return EXT_BY_CT.get(key);
        } catch (Exception ignore) {
            return null;
        }
    }

    public static String randomWithExtension(String originalFilename, String contentType) {
        String ext = extFromFilename(originalFilename);
        if (ext == null) {
            ext = Objects.requireNonNullElse(extFromContentType(contentType), "bin");
        }

        String baseName = baseNameFromFilename(originalFilename);
        if (baseName == null || baseName.isBlank()) {
            baseName = "file";
        }

        return baseName + "_" + randomBase() + "." + ext;
    }
}
