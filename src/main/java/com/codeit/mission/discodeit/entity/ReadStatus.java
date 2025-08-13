package com.codeit.mission.discodeit.entity;

import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
@ToString
public class ReadStatus implements Serializable {

    private static final long serialVersionUID = 1L;

    private UUID id;
    private Instant createdAt;
    private Instant updatedAt;

    private UUID userId;
    private UUID channelId;

    private Instant lastReadTime;

    public ReadStatus(UUID userId, UUID channelId, Instant lastReadTime) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();

        this.userId = userId;
        this.channelId = channelId;
        this.lastReadTime = lastReadTime;
    }

    public void update(Instant newLastReadTime) {
        boolean anyValueUpdated = false;
        if (newLastReadTime != null && !newLastReadTime.equals(this.lastReadTime)) {
            this.lastReadTime = newLastReadTime;
            anyValueUpdated = true;
        }

        if (anyValueUpdated) {
            this.updatedAt = Instant.now();
        }
    }
}
