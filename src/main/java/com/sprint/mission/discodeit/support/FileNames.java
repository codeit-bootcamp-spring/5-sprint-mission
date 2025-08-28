package com.sprint.mission.discodeit.support;

import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import org.springframework.http.MediaType;

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

    public static String extFromFileName(String originalFileName) {
        if (originalFileName == null || originalFileName.isBlank()) {
            return null;
        }
        String name = Paths.get(originalFileName).getFileName().toString().strip();
        int dot = name.lastIndexOf('.');
        if (dot < 0 || dot == name.length() - 1) {
            return null;
        }
        String ext = name.substring(dot + 1).toLowerCase(Locale.ROOT);
        return ext.matches("[a-z0-9]+") ? ext : null;
    }

    private static String baseNameFromFileName(String fileName) {
        if (fileName == null) {
            return null;
        }
        int dot = fileName.lastIndexOf('.');
        if (dot > 0) {
            return fileName.substring(0, dot);
        }
        return fileName;
    }

    public static String extFromContentType(String contentType) {
        if (contentType == null || contentType.isBlank()) {
            return null;
        }
        try {
            MediaType mt = MediaType.parseMediaType(contentType);
            String key = (mt.getType() + "/" + mt.getSubtype()).toLowerCase(Locale.ROOT);
            return EXT_BY_CT.get(key);
        } catch (Exception ignore) {
            return null;
        }
    }

    public static String randomWithExtension(String originalFileName, String contentType) {
        String ext = extFromFileName(originalFileName);
        if (ext == null) {
            ext = Objects.requireNonNullElse(extFromContentType(contentType), "bin");
        }

        String baseName = baseNameFromFileName(originalFileName);
        if (baseName == null || baseName.isBlank()) {
            baseName = "file";
        }

        return baseName + "_" + randomBase() + "." + ext;
    }

    public static String normalizeContentType(String ct) {
        if (ct == null || ct.isBlank()) {
            return MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }
        int semi = ct.indexOf(';');
        return (semi >= 0 ? ct.substring(0, semi) : ct).strip().toLowerCase(Locale.ROOT);
    }

    public static String buildStoredName(String original, String contentType) {
        String random = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        String ext = extFromOriginalOrContentType(original, contentType);
        String base = baseName(original);
        return base + "_" + random + ext;
    }

    public static String baseName(String original) {
        if (original == null || original.isBlank()) {
            return "file";
        }
        String name = Paths.get(original).getFileName().toString();
        int dot = name.lastIndexOf('.');
        return (dot > 0) ? name.substring(0, dot) : name;
    }

    public static String extFromOriginalOrContentType(String original, String ct) {
        if (original != null) {
            String name = Paths.get(original).getFileName().toString();
            int dot = name.lastIndexOf('.');
            if (dot > 0 && dot < name.length() - 1) {
                return name.substring(dot);
            }
        }
        return switch (ct) {
            case MediaType.IMAGE_PNG_VALUE -> ".png";
            case MediaType.IMAGE_JPEG_VALUE -> ".jpg";
            case "image/webp" -> ".webp";
            default -> "";
        };
    }
}
