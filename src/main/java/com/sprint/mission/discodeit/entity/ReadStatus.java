package com.sprint.mission.discodeit.entity;

import lombok.*;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@NoArgsConstructor
@Getter
public class ReadStatus implements Serializable {

    private UUID id;
    private UUID userId;
    private UUID channelId;
    private Instant lastReadTime;
    private Instant createdAt;
    private Instant updatedAt;

    public ReadStatus(UUID userId, UUID channelId, Instant lastReadTime) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.channelId = channelId;
        this.lastReadTime = lastReadTime;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public void updateLastReadTime(Instant readTime) {
        if (readTime != null && !readTime.equals(this.lastReadTime)) {
            this.lastReadTime = readTime;
            this.updatedAt = Instant.now();
        }

    }

}
