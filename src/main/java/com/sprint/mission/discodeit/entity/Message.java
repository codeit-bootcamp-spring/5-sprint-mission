package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.criteria.CriteriaBuilder.In;
import java.util.ArrayList;
import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "messages")
@Getter
@NoArgsConstructor
public class Message extends BaseUpdatableEntity {

  @Column(nullable = false, length = 1000)
  private String content;

  @ManyToOne
  @JoinColumn(name = "channel_id", nullable = false)
  private Channel channel;

  @ManyToOne
  @JoinColumn(name = "author_id", nullable = false)
  private User author;

  @OneToMany
  @JoinTable(
      name = "message_attachments",
      joinColumns = @JoinColumn(name = "message_id"),
      inverseJoinColumns = @JoinColumn(name = "binary_content_id")
  )
  private List<BinaryContent> attachments = new ArrayList<>();

  public Message(String content, Channel channel, User author) {
    this.content = content;
    this.channel = channel;
    this.author = author;
  }

  public void update(String newContent) {
    if (newContent != null && !newContent.equals(this.content)) {
      this.content = newContent;
      updateTimestamp(Instant.now());
    }
  }

  public void addAttachment(BinaryContent binaryContent) {
    if (binaryContent != null && !attachments.contains(binaryContent)) {
      attachments.add(binaryContent);
    }
  }

  public void removeAttachment(BinaryContent binaryContent) {
    attachments.remove(binaryContent);
  }

  public void clearAttachments() {
    attachments.clear();
  }
}
