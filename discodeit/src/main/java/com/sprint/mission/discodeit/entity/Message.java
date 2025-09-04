package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@Entity
@Table(name = "messages")
public class Message extends BaseUpdatableEntity {

//  private static final long serialVersionUID = 1L;

  //
  private String content;
  //

  @ManyToOne
  @OnDelete(action = OnDeleteAction.CASCADE)
  private Channel channel;


  @ManyToOne
  @JoinColumn(nullable = false)
  private User author;

  @OneToMany(fetch = FetchType.EAGER)
  @JoinColumn(nullable = true)
  private List<BinaryContent> attachment; //BinaryContent

//  public Message(String content, UUID channelId, UUID authorId, List<UUID> attachmentIds) {
//    this.content = content;
//    this.channelId = channelId;
//    this.authorId = authorId;
//    this.attachmentIds = attachmentIds;
//  }

  public Message(String content, Channel channel, User author, List<BinaryContent> attachment) {
    this.content = content;
    this.channel = channel;
    this.author = author;
    this.attachment = attachment;
  }

  public Message(String content, Channel channel, User author) {
    this.content = content;
    this.channel = channel;
    this.author = author;
  }

  public Message() {

  }

  public void update(String newContent) {
    boolean anyValueUpdated = false;
    if (newContent != null && !newContent.equals(this.content)) {
      this.content = newContent;
      anyValueUpdated = true;
    }

    if (anyValueUpdated) {
      this.updatedAt = Instant.now();
    }
  }
}
