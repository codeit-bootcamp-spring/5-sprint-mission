package com.sprint.mission.discodeit.entity;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

import static java.time.Instant.*;

public class Message implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final UUID id;
    private final UUID authorId;
    private final UUID channelId;
    private final Long createdAt;

    private Long updatedAt;
    private String content;

    public Message(UUID authorId, UUID channelId, String content) {
        this.id = UUID.randomUUID();
        this.authorId = authorId;
        this.channelId = channelId;
        this.createdAt = now().getEpochSecond();

        this.content = content;
        this.updatedAt = now().getEpochSecond();
    }

    public UUID getId() { return id; }
    public UUID getAuthorId() { return authorId; }
    public UUID getChannelId() { return channelId; }
    public Long getCreatedAt() { return createdAt; }
    public Long getUpdatedAt() { return updatedAt; }
    public String getContent() { return content; }

    public void update(String content) {
        boolean anyValueUpdated = false;

        if(isContentChanged(content)) {
            this.content = content;
            anyValueUpdated = true;
        }

        if(anyValueUpdated) {
            this.updatedAt = now().getEpochSecond();
        }
    }

    public boolean isContentChanged(String content) {
        return content != null && !Objects.equals(this.content, content);
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", authorId=" + authorId +
                ", channelId=" + channelId +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", content='" + content + '\'' +
                '}';
    }
}
