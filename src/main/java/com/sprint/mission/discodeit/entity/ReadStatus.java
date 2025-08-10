package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
@ToString
public class ReadStatus implements Serializable {
    private static final long serialVersionUID = 1L;

    private final UUID id;
    private final UUID userId;
    private final UUID channelId;
    private final Instant createdAt;
    private Instant updatedAt;

    public ReadStatus(UUID id, UUID userId, UUID channelId, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.userId = userId;
        this.channelId = channelId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public void update() {
        this.updatedAt = Instant.now();
    }
}
