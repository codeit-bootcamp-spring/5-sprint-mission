package com.sprint.mission.discodeit.entity.sub;

import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public class ReadStatus implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final UUID id;
    private final UUID userId;
    private final UUID channelId;

    private final Instant createdAt;
    private Instant updatedAt;
    private Instant lastReadAt;

    public ReadStatus(UUID userId, UUID channelId, Instant lastReadAt) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();

        this.userId = userId;
        this.channelId = channelId;
        this.lastReadAt = lastReadAt;
    }

    public void update(Instant newLastReadAt) {
        boolean anyValueUpdated = false;

        if (isLasReadAtChanged(newLastReadAt)) {
            this.lastReadAt = newLastReadAt;
            anyValueUpdated = true;
        }

        if (anyValueUpdated) {
            this.updatedAt = Instant.now();
        }
    }

    private boolean isLasReadAtChanged(Instant newLastReadAt) {
        return newLastReadAt != null && !newLastReadAt.equals(this.lastReadAt);
    }
}
