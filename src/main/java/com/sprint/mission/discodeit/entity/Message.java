package com.sprint.mission.discodeit.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "messages")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Message extends BaseUpdatableEntity {


  @Column(columnDefinition = "text", nullable = false)
  private String content;
  @ManyToOne(optional = false)
  @JoinColumn(name = "channel_id", nullable = false)
  private Channel channel;


  @ManyToOne
  @JoinColumn(name = "author_id")
  private User author;


  @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
  @JoinTable(name = "message_attachments", joinColumns = @JoinColumn(name = "message_id"), inverseJoinColumns = @JoinColumn(name = "attachment_id"))
  @Builder.Default
  private List<BinaryContent> attachments = new ArrayList<>();


  public void update(String newContent) {
    boolean anyValueUpdated = false;

    if (newContent != null && !newContent.equals(this.content)) {
      this.content = newContent;
      anyValueUpdated = true;
    }

    if (anyValueUpdated) {
      updateTimestamp();
    }
  }
}
