package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;

import lombok.NoArgsConstructor;


@Entity
@Table(name = "binary_contents")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BinaryContent extends BaseEntity {

  @Column(name = "file_name", nullable = false, length = 255)
  private String fileName;

  @Column(name = "size", nullable = false)
  private long size;

  @Column(name = "content_type", nullable = false, length = 100)
  private String contentType;

  @Lob
  @Column(name = "bytes", nullable = false)
  private byte[] bytes;

  public BinaryContent(String fileName, long size, String contentType, byte[] bytes) {
    this.fileName = fileName;
    this.size = size;
    this.contentType = contentType;
    this.bytes = bytes;
  }

  public void updateMeta(String fileName, long size, String contentType) {
    this.fileName = fileName;
    this.size = size;
    this.contentType = contentType;
  }
}
