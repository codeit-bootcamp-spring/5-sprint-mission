package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
@ToString
public class ReadStatus implements Serializable {
    private final UUID id;
    private final UUID userId;
    private final UUID channelId;
    private final Instant createdAt;
    private Instant updatedAt;
    private boolean read;

    public ReadStatus(UUID userId, UUID channelId) {
        id = UUID.randomUUID();
        createdAt = Instant.now();
        read = false;

        this.userId = userId;
        this.channelId = channelId;
    }

    public void update(boolean read) {
        boolean anyValueUpdated = false;
        if (this.read != read) {
            this.read = read;
            anyValueUpdated = true;
        }

        if (anyValueUpdated) {
            updatedAt = Instant.now();
        }
    }
}
