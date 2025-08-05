package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.UUID;

public class Message implements Serializable {
    private static final long serialVersionUID = 1L;

    private final UUID id;
    private final long  createdAt;
    private long updatedAt;
    private String content;
    private final String userId;
    private final UUID channelId;

    public Message(String content, String userId, UUID channelId) {
        this.content = content;
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = this.createdAt;
        this.userId = userId;
        this.channelId = channelId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
        this.updatedAt = System.currentTimeMillis();
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public UUID getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public UUID getChannelId() {
        return channelId;
    }

    public void update(String newContent) {
        this.content = newContent;
    }

    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        final StringBuilder sb = new StringBuilder("Message{");
        sb.append("id=").append(id);
        sb.append(", createdAt=").append(sdf.format(createdAt));
        sb.append(", updatedAt=").append(sdf.format(updatedAt));
        sb.append(", content='").append(content).append('\'');
        sb.append(", userId='").append(userId).append('\'');
        sb.append(", channelId=").append(channelId);
        sb.append('}');
        return sb.toString();
    }
}
