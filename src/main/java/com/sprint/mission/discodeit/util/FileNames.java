package com.sprint.mission.discodeit.util;

import org.springframework.http.MediaType;

import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public final class FileNames {

    private FileNames() {
    }

    private static final SecureRandom RNG = new SecureRandom();

    // Content-Type -> 확장자 매핑 (필요 시 확장)
    private static final Map<String, String> EXT_BY_CT = Map.ofEntries(
            Map.entry("image/png", "png"),
            Map.entry("image/jpeg", "jpg"),
            Map.entry("image/gif", "gif"),
            Map.entry("image/webp", "webp"),
            Map.entry("image/svg+xml", "svg"),
            Map.entry("application/pdf", "pdf"),
            Map.entry("application/octet-stream", "bin")
    );

    /**
     * 랜덤 베이스 이름 (URL-safe, 16바이트 ~ 22자 내외)
     */
    public static String randomBase() {
        byte[] buf = new byte[16];
        RNG.nextBytes(buf);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(buf);
    }

    /**
     * 입력 파일명에서 확장자 추출 (없으면 null)
     */
    public static String extFromFilename(String originalFilename) {
        if (originalFilename == null || originalFilename.isBlank()) return null;
        // 경로 제거 + 트릭 문자 제거
        String name = Paths.get(originalFilename).getFileName().toString().trim();
        int dot = name.lastIndexOf('.');
        if (dot < 0 || dot == name.length() - 1) return null;
        String ext = name.substring(dot + 1).toLowerCase(Locale.ROOT);
        // 알파벳/숫자만 허용(보안상)
        return ext.matches("[a-z0-9]+") ? ext : null;
    }

    /**
     * Content-Type에서 확장자 추정 (모르면 null)
     */
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

    /**
     * 랜덤 파일명 + 확장자 결정 (둘 다 없으면 .bin)
     */
    public static String randomWithExtension(String originalFilename, String contentType) {
        String ext = Objects.requireNonNullElseGet(
                extFromFilename(originalFilename),
                () -> extFromContentType(contentType)
        );
        if (ext == null) ext = "bin";
        return randomBase() + "." + ext;
    }
}
