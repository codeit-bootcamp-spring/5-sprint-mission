package com.sprint.mission.discodeit.domain.entity;

import lombok.Getter;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.HexFormat;
import java.util.Objects;
import java.util.UUID;

@Getter
public class BinaryContent extends BaseEntity {

    private String filename;
    private String contentType;
    private long size;
    private String sha256;
    private String storagePath;

    private byte[] bytes;

    public BinaryContent(String filename, String contentType, byte[] bytes) {
        setAll(normalizeFilename(requireNonBlank(filename, "filename must not be blank")),
                requireNonBlank(contentType, "contentType must not be blank"),
                Objects.requireNonNull(bytes, "bytes must not be null"),
                null);
    }

    protected BinaryContent(UUID id, Instant createdAt, String filename, String contentType, byte[] bytes, String storagePath) {
        super(id, createdAt);
        setAll(normalizeFilename(requireNonBlank(filename, "filename must not be blank")),
                requireNonBlank(contentType, "contentType must not be blank"),
                Objects.requireNonNull(bytes, "bytes must not be null"),
                storagePath);
    }

    public static BinaryContent of(String filename, String contentType, byte[] bytes) {
        return new BinaryContent(filename, contentType, bytes);
    }

    public static BinaryContent fromMultipart(MultipartFile file) throws IOException {
        String name = normalizeFilename(requireNonBlank(file.getOriginalFilename(), "originalFilename must not be blank"));
        String type = guessContentType(file.getContentType(), name);
        return new BinaryContent(name, type, file.getBytes());
    }

    public static BinaryContent fromOctetStream(String filename, String contentType, byte[] bytes) {
        return of(filename, guessContentType(contentType, filename), bytes);
    }

    public void updateBytes(String filename, String contentType, byte[] bytes) {
        setAll(normalizeFilename(requireNonBlank(filename, "filename must not be blank")),
                requireNonBlank(contentType, "contentType must not be blank"),
                Objects.requireNonNull(bytes, "bytes must not be null"),
                this.storagePath);
    }

    public void updateStoragePath(String storagePath) {
        this.storagePath = storagePath;
        touch();
    }

    public void wipe() {
        if (this.bytes != null) {
            this.bytes = null;
            touch();
        }
    }

    public byte[] getBytes() {
        return (bytes == null) ? null : bytes.clone();
    }

    private void setAll(String filename, String contentType, byte[] bytes, String storagePath) {
        this.filename = filename;
        this.contentType = contentType;
        this.size = bytes.length;
        this.sha256 = sha256Hex(bytes);
        this.bytes = bytes.clone();
        this.storagePath = storagePath;
        touch();
    }

    private static String requireNonBlank(String s, String msg) {
        if (s == null || s.isBlank()) throw new IllegalArgumentException(msg);
        return s;
    }

    private static String normalizeFilename(String raw) {
        String name = raw.replace('\\', '/');
        int idx = name.lastIndexOf('/');
        return (idx >= 0) ? name.substring(idx + 1) : name;
    }

    private static String guessContentType(String contentType, String filename) {
        if (contentType != null && !contentType.isBlank()) return contentType;
        String lower = filename.toLowerCase();
        if (lower.endsWith(".png")) return MediaType.IMAGE_PNG_VALUE;
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) return MediaType.IMAGE_JPEG_VALUE;
        if (lower.endsWith(".gif")) return MediaType.IMAGE_GIF_VALUE;
        if (lower.endsWith(".pdf")) return "application/pdf";
        return MediaType.APPLICATION_OCTET_STREAM_VALUE;
    }

    private static String sha256Hex(byte[] bytes) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(md.digest(bytes));
        } catch (Exception e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }

    @Override
    public String toString() {
        return "BinaryContent[id=%s, filename=%s, contentType=%s, size=%d, sha256=%s, storagePath=%s]"
                .formatted(getId(), filename, contentType, size, sha256, storagePath);
    }
}
