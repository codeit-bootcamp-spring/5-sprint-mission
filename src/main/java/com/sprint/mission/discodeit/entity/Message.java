package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import lombok.Getter;

@Entity
@Table(name = "messages")
@Getter
public class Message extends BaseUpdatableEntity {

  /* 여러 메세지는 한명의 작성자를 참조 N:1
   * FK 역할
   * */
  @ManyToOne
  @JoinColumn(name = "author_id")
  private User author; // 채널 기준으로 누가 보냈는지


  /* 여러 메세지는 한개의 채널을 참조 N:1
   * FK 역할
   */
  @ManyToOne
  @JoinColumn(name = "channel_id")
  private Channel channel; // 채널 ID


  /* 한개의 메세지는 여러개의 첨부파일을 참조 1:N
   *FK 역할
   */
  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "message_id")
  private List<BinaryContent> attachments;

  @Column
  private String content; //메세지 내용


  //기본생성자
  public Message() {
    super();
  }


  //일반 생성자 - 사용자로부터 받는 값
  public Message(String content, User author, Channel channel) {
    this.content = content;
    this.author = author;
    this.channel = channel;
  }

  //복사생성자 - 메세지 객체 안의 있는 값들 복사해서 새로운 Message 만듦
  public Message(Message other) {
    this.content = other.content;
    this.author = other.author;
    this.channel = other.channel;
  }

  //메서드
  public void updateTime() {
  }


  // 메시지 내용 수정
  public void updateContent(String newContent) {
    if (newContent == null || newContent.trim().isEmpty()) {
      throw new IllegalArgumentException("내용은 비어 있을 수 없습니다.");
    }
    this.content = newContent;
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



