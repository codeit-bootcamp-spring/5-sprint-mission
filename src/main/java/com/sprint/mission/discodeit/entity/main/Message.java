package com.sprint.mission.discodeit.entity.main;

import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import static java.time.Instant.*;

@Getter
public class Message implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final UUID id;

    private final UUID authorId;
    private final UUID channelId;

    private String content;

    private final Instant createdAt;
    private Instant updatedAt;

    public Message(UUID authorId, UUID channelId, String content) {
        this.id = UUID.randomUUID();
        this.authorId = authorId;
        this.channelId = channelId;
        this.content = content;
        this.createdAt = now();
        this.updatedAt = this.createdAt;
    }

    public void update(String content) {
        boolean anyValueUpdated = false;

        if(isContentChanged(content)) {
            this.content = content;
            anyValueUpdated = true;
        }

        if(anyValueUpdated) {
            this.updatedAt = now();
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
