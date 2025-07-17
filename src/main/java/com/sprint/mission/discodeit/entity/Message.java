package com.sprint.mission.discodeit.entity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Message {
  private final UUID id;
  private final long createdAt;
  private final User sender;
  private final User receiver;
  private final List<UUID> replies;
  private long updatedAt;
  private String content;
  private List<File> files;
  private Survey survey;

  public Message(User sender, User receiver, String content, List<File> files, Survey survey) {
    this.id = UUID.randomUUID();
    this.createdAt = System.currentTimeMillis();
    this.updatedAt = this.createdAt;
    this.sender = sender;
    this.receiver = receiver;
    this.content = content;
    this.files = files;
    this.survey = survey;
    this.replies = new ArrayList<>();
  }

  public UUID getId() {
    return id;
  }

  public long getCreatedAt() {
    return createdAt;
  }

  public long getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(long updatedAt) {
    this.updatedAt = updatedAt;
  }

  public User getSender() {
    return sender;
  }

  public User getReceiver() {
    return receiver;
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

  public void clearReplies() {
    replies.clear();
  }
}
