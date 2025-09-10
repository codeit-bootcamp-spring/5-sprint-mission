package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Auditable;

@Entity
@Table(name="binary_contents")
@Getter @Setter
@Builder
//@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BinaryContent extends BaseEntity{


  @Column(name="file_name", nullable = false, length = 255)
  private String fileName;
  @Column(name = "size", nullable = false)
  private Long size;
  @Column(name= "content_type",nullable = false, length = 100)
  private String contentType;
//  @Column(name = "bytes",nullable = false)
//  private byte[] bytes;

  public BinaryContent(String fileName, Long size, String contentType) {

    this.fileName = fileName;
    this.size = size;
    this.contentType = contentType;
//    this.bytes = bytes;
  }
}
