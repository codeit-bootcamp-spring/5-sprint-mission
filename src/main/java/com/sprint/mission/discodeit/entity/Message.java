package com.sprint.mission.discodeit.entity;

import java.util.Objects;

public class Message {
    private String content;
    private final String channelName;
    private final String creatorUserId;
    private final Long createdAt;
    private Long updatedAt;

    public Message(String content, String channelName, String creatorUserId) {
        long now = System.currentTimeMillis();
        this.content = content;
        this.channelName = channelName;
        this.creatorUserId = creatorUserId;
        this.createdAt = now;
        this.updatedAt = now;
    }

    public String getContent() {
        return content;
    }

    public String getChannelName() {
        return channelName;
    }

    public String getCreatorUserId() {
        return creatorUserId;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setUpdatedAt(Long updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Message{");
        sb.append("content='").append(content).append('\'');
        sb.append(", channelName='").append(channelName).append('\'');
        sb.append(", creatorUserId='").append(creatorUserId).append('\'');
        sb.append(", createdAt=").append(createdAt);
        sb.append(", updatedAt=").append(updatedAt);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return Objects.equals(content, message.content) && Objects.equals(channelName, message.channelName) && Objects.equals(creatorUserId, message.creatorUserId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(content, channelName, creatorUserId);
    }
}
