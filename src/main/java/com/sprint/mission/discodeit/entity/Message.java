package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
@ToString
public class Message implements Serializable {
    @Serial private static final long serialVersionUID = 1L;

    private final UUID id;
    private final Instant createdAt;
    private Instant updatedAt;

    private final UUID userId;     // 작성자
    private final UUID channelId;  // 채널
    private String content;

    public Message(UUID userId, UUID channelId, String content) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
        this.userId = userId;
        this.channelId = channelId;
        this.content = content;
    }

    public void updateContent(String newContent) {
        if (newContent != null && !newContent.equals(this.content)) {
            this.content = newContent;
            this.updatedAt = Instant.now();
        }
    }
}