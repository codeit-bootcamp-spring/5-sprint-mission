package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class Message implements Serializable {

    private UUID id; // 메시지 고유 id
    private Long createdAt; // 생성 시간
    private Long updatedAt; // 수정 시간
    private String content;
    private UUID channelId;
    private UUID authorId;

    public Message(String content, UUID channelId, UUID authorId) {
        id = UUID.randomUUID();
        this.content = content;
        this.channelId = channelId;
        this.authorId = authorId;
        createdAt = System.currentTimeMillis();
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

    public String getContent() {
        return content;
    }

    public UUID getChannelId() {
        return channelId;
    }

    public UUID getAuthorId() {
        return authorId;
    }

    public Message update(String content) {
        this.content = content;
        this.updatedAt = System.currentTimeMillis();
        return this;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", content='" + content + '\'' +
                ", channelId=" + channelId +
                ", authorId=" + authorId +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return Objects.equals(id, message.id) && Objects.equals(createdAt, message.createdAt) && Objects.equals(updatedAt, message.updatedAt) && Objects.equals(content, message.content) && Objects.equals(channelId, message.channelId) && Objects.equals(authorId, message.authorId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, createdAt, updatedAt, content, channelId, authorId);
    }
}
