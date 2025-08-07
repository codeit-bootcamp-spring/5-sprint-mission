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
    private final UUID channelId;
    private final UUID userId;
    private final Instant createdAt;
    private Instant updatedAt;
    private Instant lastReadAt;

    public ReadStatus(UUID id, UUID channelId, UUID userId, Instant lastReadAt, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.channelId = channelId;
        this.userId = userId;
        this.lastReadAt = lastReadAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public ReadStatus(UUID userId, UUID channelId, Instant lastReadAt) {
        this(UUID.randomUUID(), channelId, userId, lastReadAt, Instant.now(), Instant.now());
    }

    public void update(Instant lastReadAt) {
        this.lastReadAt = lastReadAt;
        this.updatedAt = Instant.now();
    }
}
