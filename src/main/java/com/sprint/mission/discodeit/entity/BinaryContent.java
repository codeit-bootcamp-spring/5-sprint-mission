package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "binary_contents")
@Data
@Getter
@NoArgsConstructor
public class BinaryContent extends BaseUpdatableEntity {

  @Column(name = "file_name", length = 255, nullable = false)
  private String fileName; // 파일 이름

  @Column(name = "size", nullable = false)
  private long size; // 파일 사이즈

  @Column(name = "content_type", length = 100, nullable = false)
  private String contentType; // 파일 타입


  @Column(name = "bytes", nullable = false)
  private byte[] bytes; // 실제 파일 데이터

  //기본 생성자
  public BinaryContent(String fileName,
      String contentType, long size, byte[] bytes) {
    this.fileName = fileName;
    this.contentType = contentType;
    this.size = size;
    this.bytes = bytes;
  }

  public BinaryContent(String fileName,
      String contentType, long size) {

    this.fileName = fileName;
    this.contentType = contentType;
    this.size = size;
  }
}
