package com.sprint.mission.discodeit.entity;

import java.util.UUID;

// 공통 부모 클래스 정의
public abstract class user {
    protected UUID id;
    protected Long createdAt;
    protected Long updatedAt;

    public user() {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = this.createdAt;
    }

    public UUID getId() {
        return id;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public void updateTimestamp() {
        this.updatedAt = System.currentTimeMillis();
    }
}

// User 도메인 모델
class User extends user {
    private String username;

    public User(String username) {
        super();
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void updateUsername(String username) {
        this.username = username;
        updateTimestamp();
    }
}

// Channel 도메인 모델
class Channel extends user {
    private String name;

    public Channel(String name) {
        super();
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void updateName(String name) {
        this.name = name;
        updateTimestamp();
    }
}

// Message 도메인 모델
class Message extends user {
    private String content;
    private UUID userId;
    private UUID channelId;

    public Message(String content, UUID userId, UUID channelId) {
        super();
        this.content = content;
        this.userId = userId;
        this.channelId = channelId;
    }

    public String getContent() {
        return content;
    }

    public UUID getUserId() {
        return userId;
    }

    public UUID getChannelId() {
        return channelId;
    }

    public void updateContent(String content) {
        this.content = content;
        updateTimestamp();
    }
}

