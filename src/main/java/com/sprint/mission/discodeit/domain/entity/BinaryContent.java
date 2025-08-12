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

    // ===== Identity / Metadata =====
    private String filename;        // 정규화된 파일명 (경로 제거)
    private String contentType;     // 예: image/png
    private long size;              // 바이트 크기
    private String sha256;          // 콘텐츠 체크섬 (중복·무결성)
    private String storagePath;     // 파일 저장소 상대 경로 (File 저장소 사용 시)

    // ===== Payload (메모리 보관이 필요할 때만) =====
    private byte[] bytes;

    // ===== Constructors / Factories =====
    public BinaryContent(String filename, String contentType, byte[] bytes) {
        setAll(normalizeFilename(requireNonBlank(filename, "filename must not be blank")),
                requireNonBlank(contentType, "contentType must not be blank"),
                Objects.requireNonNull(bytes, "data must not be null"),
                null);
    }

    protected BinaryContent(UUID id, Instant createdAt, String filename, String contentType, byte[] bytes, String storagePath) {
        super(id, createdAt);
        setAll(normalizeFilename(requireNonBlank(filename, "filename must not be blank")),
                requireNonBlank(contentType, "contentType must not be blank"),
                Objects.requireNonNull(bytes, "data must not be null"),
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

    // ===== Domain Behaviors =====

    /**
     * 파일 바이트/메타데이터를 교체합니다.
     */
    public void updateData(String filename, String contentType, byte[] data) {
        setAll(normalizeFilename(requireNonBlank(filename, "filename must not be blank")),
                requireNonBlank(contentType, "contentType must not be blank"),
                Objects.requireNonNull(data, "data must not be null"),
                this.storagePath);
    }

    /**
     * 파일 저장 경로(파일 저장소 상대 경로)를 기록/변경합니다.
     */
    public void updateStoragePath(String storagePath) {
        this.storagePath = storagePath;
        touch();
    }

    /**
     * 메모리 상의 payload를 지웁니다(디스크 저장 완료 후 메모리 해제 등).
     */
    public void wipe() {
        if (this.bytes != null) {
            // 민감 데이터라면 0 overwrite 고려
            this.bytes = null;
            touch();
        }
    }

    // ===== Getters (안전 사본) =====
    public byte[] getBytes() {
        return (bytes == null) ? null : bytes.clone();
    }

    // ===== Internal Helpers =====
    private void setAll(String filename, String contentType, byte[] data, String storagePath) {
        this.filename = filename;
        this.contentType = contentType;
        this.size = data.length;
        this.sha256 = sha256Hex(data);
        this.bytes = data.clone();           // 엔티티 외부 불변성 유지
        this.storagePath = storagePath;
        touch();
    }

    private static String requireNonBlank(String s, String msg) {
        if (s == null || s.isBlank()) throw new IllegalArgumentException(msg);
        return s;
    }

    private static String normalizeFilename(String raw) {
        // 일부 클라이언트가 경로 전체를 보낼 수 있으므로 파일명만 추출
        String name = raw.replace('\\', '/');
        int idx = name.lastIndexOf('/');
        return (idx >= 0) ? name.substring(idx + 1) : name;
    }

    private static String guessContentType(String contentType, String filename) {
        if (contentType != null && !contentType.isBlank()) return contentType;
        // 간단한 추정 로직(필요 시 확장)
        String lower = filename.toLowerCase();
        if (lower.endsWith(".png")) return MediaType.IMAGE_PNG_VALUE;
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) return MediaType.IMAGE_JPEG_VALUE;
        if (lower.endsWith(".gif")) return MediaType.IMAGE_GIF_VALUE;
        if (lower.endsWith(".pdf")) return "application/pdf";
        return MediaType.APPLICATION_OCTET_STREAM_VALUE;
    }

    private static String sha256Hex(byte[] data) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(md.digest(data));
        } catch (Exception e) {
            // 이례적 상황: JRE 보장 알고리즘
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }

    // ===== Object =====
    @Override
    public String toString() {
        return "BinaryContent[id=%s, filename=%s, contentType=%s, size=%d, sha256=%s, storagePath=%s]"
                .formatted(getId(), filename, contentType, size, sha256, storagePath);
    }
}
