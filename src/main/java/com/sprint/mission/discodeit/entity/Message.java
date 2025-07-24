package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.utility.Validators;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Message extends BaseEntity {
  private final UUID senderId;
  private final UUID receiverId;
  private String content;
  private List<String> files;
  private Survey survey;
  private final List<UUID> replies = new ArrayList<>();

  public Message(
      UUID senderId, UUID receiverId, String content, List<String> files, Survey survey) {
    if (senderId == null || receiverId == null) {
      throw new IllegalArgumentException("Sender and receiver must not be null.");
    }
    this.senderId = senderId;
    this.receiverId = receiverId;
    setContent(content);
    setFiles(files);
    setSurvey(survey);
  }

  public Message(UUID senderId, UUID receiverId, String content, List<String> files) {
    this(senderId, receiverId, content, files, null);
  }

  public Message(UUID senderId, UUID receiverId, String content) {
    this(senderId, receiverId, content, null);
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
    if (content == null) {
      throw new IllegalArgumentException("Content must not be null.");
    }
    this.content = content;
  }

  public List<String> getFiles() {
    return Collections.unmodifiableList(files);
  }

  public void setFiles(List<String> files) {
    if (files == null) {
      this.files = new ArrayList<>();
    } else {
      for (String file : files) {
        Validators.validateUri(file);
      }
      this.files = new ArrayList<>(files);
    }
  }

  public Survey getSurvey() {
    return survey;
  }

  public void setSurvey(Survey survey) {
    if (survey == null) {
      this.survey = null;
    } else {
      if (!survey.getSenderId().equals(senderId)) {
        throw new IllegalStateException("survey senderId is not equal to message senderId.");
      }
      this.survey = survey;
    }
  }

  public List<UUID> getReplies() {
    return Collections.unmodifiableList(replies);
  }

  public void addReply(UUID replyId) {
    if (replyId == null) {
      throw new IllegalArgumentException("Reply ID must not be null.");
    }
    replies.add(replyId);
  }

  public void removeReply(UUID replyId) {
    if (replyId == null) {
      throw new IllegalArgumentException("replyId must not be null.");
    }
    replies.remove(replyId);
  }

  @Override
  public String toString() {
    return "Message{"
        + "sender="
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
