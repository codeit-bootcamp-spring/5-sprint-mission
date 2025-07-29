package com.sprint.mission.discodeit.entity;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class Message implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    public Message(){

    }

    @Override
    public int hashCode() {
        return Objects.hash(id, createdAt, updatedAt, content, channelId, authorId);
    }

    private UUID id;
    private Long createdAt;
    private Long updatedAt;

    private String content;
    private UUID channelId;
    private UUID authorId;

    public String getContent() {
        return content;
    }

    public void updateContent(String content) {
        this.content = content;
    }

    public UUID getChannelId() {
        return channelId;
    }

    public void updateChannelId(UUID channelId) {
        this.channelId = channelId;
    }

    public UUID getAuthorId() {
        return authorId;
    }

    public void updateAuthorId(UUID authorId) {
        this.authorId = authorId;
    }

    public UUID getId() {
        return id;
    }

    public void updateId(UUID id) {
        this.id = id;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void updateCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void updateUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }


    public Message(String content, UUID channelId, UUID authorId) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now().toEpochMilli();
        this.content = content;
        this.channelId = channelId;
        this.authorId = authorId;
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


}
