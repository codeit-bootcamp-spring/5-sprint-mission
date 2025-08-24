package com.sprint.mission.discodeit.domain.entity;

import static com.sprint.mission.discodeit.support.StringUtil.requireNonBlank;

import lombok.Getter;

@Getter
public class BinaryContent extends AbstractEntity {

  private String fileName;
  private long size;
  private String contentType;

  private byte[] bytes;

  public BinaryContent(String fileName, String contentType, byte[] bytes) {
    this.fileName = requireNonBlank(fileName, "fileName must not be blank");
    this.contentType = requireNonBlank(contentType, "contentType must not be blank");
    this.size = bytes.length;
    this.bytes = bytes.clone();
  }

  public BinaryContent update(String newFilename, String newContentType, byte[] newBytes) {
    boolean changed = false;
    if (fileName != null && !fileName.isBlank() && !fileName.equals(newFilename)) {
      this.fileName = newFilename;
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
    return "BinaryContent[id=%s, fileName=%s, contentType=%s, size=%d]"
        .formatted(getId(), fileName, contentType, size);
  }
}
