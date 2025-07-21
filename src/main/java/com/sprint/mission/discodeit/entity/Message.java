package com.sprint.mission.discodeit.entity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Message extends BaseEntity {
  private final UUID senderId;
  private final UUID receiverId;
  private String content;
  private List<File> files;
  private Survey survey;
  private final List<UUID> replies = new ArrayList<>();

  public Message(UUID senderId, UUID receiverId, String content, List<File> files, Survey survey) {
    this.senderId = senderId;
    this.receiverId = receiverId;
    this.content = content;
    this.files = files;
    this.survey = survey;
  }

  public UUID getSenderId() {
    return senderId;
  }

  public UUID getReceiverId() {
    return receiverId;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public List<File> getFiles() {
    return files;
  }

  public void setFiles(List<File> files) {
    this.files = files;
  }

  public Survey getSurvey() {
    return survey;
  }

  public void setSurvey(Survey survey) {
    this.survey = survey;
  }

  public List<UUID> getReplies() {
    return replies;
  }

  public void addReply(UUID replyId) {
    replies.add(replyId);
  }

  public void removeReply(UUID replyId) {
    replies.remove(replyId);
  }

  @Override
  public String toString() {
    return "Message{"
        + "id="
        + getId()
        + ", createdAt="
        + getCreatedAt()
        + ", updatedAt="
        + getUpdatedAt()
        + ", sender="
        + senderId
        + ", receiver="
        + receiverId
        + ", replies="
        + replies
        + ", content='"
        + content
        + '\''
        + ", files="
        + files
        + ", survey="
        + survey
        + '}';
  }
}
