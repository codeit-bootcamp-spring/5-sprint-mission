package com.sprint.mission.discodeit.entity.main;

import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.List;
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

    private List<UUID> attachmentIds;

    private final Instant createdAt;
    private Instant updatedAt;


    public Message(String content, UUID channelId, UUID authorId, List<UUID> attachmentIds) {
        this.id = UUID.randomUUID();
        this.createdAt = now();

        this.content = content;
        this.channelId = channelId;
        this.authorId = authorId;
        this.attachmentIds = attachmentIds;
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

    public boolean isContentChanged(String newContent) {
        return newContent != null && !Objects.equals(this.content, newContent);
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
