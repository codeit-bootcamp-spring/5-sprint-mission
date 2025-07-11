package com.sprint.mission.discodeit.entity;

import java.io.File;
import java.util.List;
import java.util.UUID;

public class Message {
    private final UUID id;
    private long createdAt;
    private long updatedAt;
    private User sender;
    private User receiver;
    private String content;
    private List<File> files;
    private Survey survey;
    private List<Message> replies;

    public Message(User sender, User receiver, String content, List<File> files, Survey survey, List<Message> replies) {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
        this.files = files;
        this.survey = survey;
        this.replies = replies;
    }

    public Message(User sender, User receiver, String content, List<File> files, Survey survey) {
        this(sender, receiver, content, files, survey, null);
    }

    public Message(User sender, User receiver, String content, List<File> files) {
        this(sender, receiver, content, files, null, null);
    }

    public Message(User sender, User receiver, String content) {
        this(sender, receiver, content, null, null, null);
    }

    public UUID getId() {
        return id;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
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

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
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

    public List<Message> getReplies() {
        return replies;
    }

    public void setReplies(List<Message> replies) {
        this.replies = replies;
    }
}
