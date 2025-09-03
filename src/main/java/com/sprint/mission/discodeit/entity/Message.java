package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Getter;

@Entity
@Table(name = "messages")
@Getter
public class Message extends BaseUpdatableEntity {

  @ManyToOne
  @JoinColumn(name = "author_id")
  private User author; // 채널 기준으로 누가 보냈는지


  /* 메세지가 채널을 참조
   * FK 역할
   */
  @ManyToOne
  @JoinColumn(name = "channel_id")
  private Channel channel; // 채널 ID


  /* 메세지와 첨부파일은 다대다 관계라
   * JPA가 중간 테이블 message_attachments 자동생성
   * */
  @ManyToMany
  @JoinTable(
      name = "message_attachments",
      joinColumns = @JoinColumn(name = "message_id"), // Message 참조하는 FK
      inverseJoinColumns = @JoinColumn(name = "file_id") // BinaryContent 참조하는 FK
  )
  private List<BinaryContent> attachments = new ArrayList<>(); // attachment list로 관리


  @Column()
  private String content; //메세지 내용


  //기본생성자
  public Message() {
    super();
  }


  //일반 생성자
  //사용자로부터 받는 값
  public Message(String content, User author, Channel channel) {
    this.content = content;
    this.author = author;
    this.channel = channel;
  }

  //복사생성자
  //메세지 객체 안의 있는 값들 복사해서 새로운 Message 만듦
  public Message(Message other) {
    this.content = other.content;
    this.author = other.author;
    this.channel = other.channel;
  }

  //메서드
  public void updateTime() {
  }


  public void setAttachmentIds(List<UUID> attachmentIds) {
    this.attachments = attachments;
  }


  public void setContent(String content) {
    this.content = content;
  }

  //toString
  @Override
  public String toString() {
    return "Message{" +
        "content='" + content + '\'' +
        ", channel=" + (channel != null ? channel.getId() : null) +
        ", author=" + (author != null ? author.getId() : null) +
        ", attachmentIds=" + attachments +
        "} " + super.toString();
  }

}



