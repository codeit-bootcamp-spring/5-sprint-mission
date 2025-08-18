package com.sprint.mission.discodeit.domain.entity;

import java.io.IOException;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.HexFormat;
import java.util.Objects;
import java.util.UUID;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
public class BinaryContent extends AbstractEntity {

  private static final String IMAGE_PNG = "image/png";
  private static final String IMAGE_JPEG = "image/jpeg";
  private static final String IMAGE_GIF = "image/gif";
  private static final String APP_OCTET = "application/octet-stream";

  private String filename;
  private String contentType;
  private long size;
  private String sha256;
  private String storagePath;

  private byte[] bytes;

  public BinaryContent(String filename, String contentType, byte[] bytes) {
    setAll(
        normalizeFilename(requireNonBlank(filename, "filename must not be blank")),
        normalizeContentType(requireNonBlank(contentType, "contentType must not be blank")),
        Objects.requireNonNull(bytes, "bytes must not be null"),
        null
    );
  }

  protected BinaryContent(UUID id, Instant createdAt, String filename, String contentType,
      byte[] bytes, String storagePath) {
    super(id, createdAt);
    setAll(
        normalizeFilename(requireNonBlank(filename, "filename must not be blank")),
        normalizeContentType(requireNonBlank(contentType, "contentType must not be blank")),
        Objects.requireNonNull(bytes, "bytes must not be null"),
        storagePath
    );
  }

  public static BinaryContent fromMultipart(MultipartFile file) throws IOException {
    String name = normalizeFilename(
        requireNonBlank(file.getOriginalFilename(), "originalFilename must not be blank"));
    String type = guessContentType(file.getContentType(), name);
    return new BinaryContent(name, type, file.getBytes());
  }

  public static BinaryContent fromOctetStream(String filename, String contentType, byte[] bytes) {
    return new BinaryContent(filename, guessContentType(contentType, filename), bytes);
  }

  public void updateBytes(String filename, String contentType, byte[] bytes) {
    setAll(
        normalizeFilename(requireNonBlank(filename, "filename must not be blank")),
        normalizeContentType(requireNonBlank(contentType, "contentType must not be blank")),
        Objects.requireNonNull(bytes, "bytes must not be null"),
        this.storagePath
    );
  }

  public void updateStoragePath(String storagePath) {
    if (!Objects.equals(this.storagePath, storagePath)) {
      this.storagePath = storagePath;
      touch();
    }
  }

  public void markStored(String storagePath, boolean wipeBytes) {
    updateStoragePath(storagePath);
    if (wipeBytes) {
      wipe();
    }
  }

  public void wipe() {
    if (this.bytes != null) {
      this.bytes = null;
      touch();
    }
  }

  public boolean isStored() {
    return this.storagePath != null && !this.storagePath.isBlank();
  }

  public boolean hasSameContentAs(BinaryContent other) {
    if (other == null) {
      return false;
    }
    if (this.size != other.size) {
      return false;
    }
    return Objects.equals(this.sha256, other.sha256);
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
    if (s == null || s.isBlank()) {
      throw new IllegalArgumentException(msg);
    }
    return s;
  }

  private static String normalizeFilename(String raw) {
    String name = raw.replace('\\', '/');
    int idx = name.lastIndexOf('/');
    name = (idx >= 0) ? name.substring(idx + 1) : name;
    name = name.chars()
        .filter(c -> c >= 32)
        .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
        .toString()
        .trim();
    if (name.isEmpty()) {
      throw new IllegalArgumentException("filename must not be blank after normalization");
    }
    if (name.length() > 255) {
      name = name.substring(0, 255);
    }
    return name;
  }

  private static String normalizeContentType(String ct) {
    return ct.trim().toLowerCase();
  }

  private static String guessContentType(String contentType, String filename) {
    if (contentType != null && !contentType.isBlank()) {
      return normalizeContentType(contentType);
    }
    String lower = filename.toLowerCase();
    if (lower.endsWith(".png")) {
      return IMAGE_PNG;
    }
    if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) {
      return IMAGE_JPEG;
    }
    if (lower.endsWith(".gif")) {
      return IMAGE_GIF;
    }
    return APP_OCTET;
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
