package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "binary_contents")
@Getter
@NoArgsConstructor
public class BinaryContent extends BaseUpdatableEntity {

  /* 파일의 이름, 사이즈, 타입, 파일 데이터를 가짐
   * 메시지에 첨부되는 파일로 연결될 수 있음
   */

  // 파일 이름
  @Column(name = "file_name", length = 255, nullable = false)
  private String fileName;

  // 파일 사이즈
  @Column(name = "size", nullable = false)
  private long size;

  // 파일 타입
  @Column(name = "content_type", length = 100, nullable = false)
  private String contentType;


  // 실제 파일 데이터
  @Column(name = "bytes", nullable = false)
  private byte[] bytes;

  // 여러개의 파일은 하나의 메세지에 첨부될 수 있음 N:1
  @ManyToOne
  @JoinColumn(name = "message_id")
  private Message message;


  //일반생성자 - 파일 등록할때 필요한 값만 받음
  public BinaryContent(String fileName,
      String contentType, long size, byte[] bytes) {
    this.fileName = fileName;
    this.contentType = contentType;
    this.size = size;
    this.bytes = bytes;
  }

  public void setMessage(Message message) {
    this.message = message;
  }
}
