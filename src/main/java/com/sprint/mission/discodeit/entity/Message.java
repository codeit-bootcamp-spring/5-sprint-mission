package com.sprint.mission.discodeit.entity;

import java.io.File;
import java.util.UUID;

public class Message {
    private final UUID id;
    private long createdAt;
    private long updatedAt;
    private User sender;
    private User receiver;
    private String content;
    private File[] files;
    private Survey survey;
    private Message[] replies;

    public Message(User sender, User receiver, String content, File[] files, Survey survey, Message[] replies) {
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

    public Message(User sender, User receiver, String content, File[] files, Survey survey) {
        this(sender, receiver, content, files, survey, null);
    }

    public Message(User sender, User receiver, String content, File[] files) {
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

    public File[] getFiles() {
        return files;
    }

    public void setFiles(File[] files) {
        this.files = files;
    }

    public Survey getSurvey() {
        return survey;
    }

    public void setSurvey(Survey survey) {
        this.survey = survey;
    }

    public Message[] getReplies() {
        return replies;
    }

    public void setReplies(Message[] replies) {
        this.replies = replies;
    }
}
