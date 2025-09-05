package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Message extends BaseUpdatableEntity {
  private String content;
  private Channel channel;                 // 1
  private User author;                     // 1
  private final List<BinaryContent> attachments = new ArrayList<>(); // *

  public Message(String content, Channel channel, User author) {
    this.content = content;
    this.channel = channel;
    this.author = author;
  }

  public void update(String content) { this.content = content; }

  public void addAttachment(BinaryContent file) {
    if (file != null) attachments.add(file);
  }

  public void removeAttachment(BinaryContent file) {
    attachments.remove(file);
  }
}
