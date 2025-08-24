package com.sprint.mission.discodeit.domain.entity;

import static com.sprint.mission.discodeit.support.StringUtil.requireNonBlank;

import lombok.Getter;

@Getter
public class BinaryContent extends AbstractEntity {

  private String filename;
  private long size;
  private String contentType;

  private byte[] bytes;

  public BinaryContent(String filename, String contentType, byte[] bytes) {
    this.filename = requireNonBlank(filename, "filename must not be blank");
    this.contentType = requireNonBlank(contentType, "contentType must not be blank");
    this.size = bytes.length;
    this.bytes = bytes.clone();
  }

  public BinaryContent update(String newFilename, String newContentType, byte[] newBytes) {
    boolean changed = false;
    if (filename != null && !filename.isBlank() && !filename.equals(newFilename)) {
      this.filename = newFilename;
    }
    if (contentType != null && !contentType.isBlank() && !contentType.equals(newContentType)) {
      this.contentType = newContentType;
      changed = true;
    }
    if (newBytes != null) {
      this.size = newBytes.length;
      this.bytes = newBytes.clone();
      changed = true;
    }
    if (changed) {
      touch();
    }
    return this;
  }

  public byte[] getBytes() {
    return (bytes == null) ? null : bytes.clone();
  }

  @Override
  public String toString() {
    return "BinaryContent[id=%s, filename=%s, contentType=%s, size=%d]"
        .formatted(getId(), filename, contentType, size);
  }
}
