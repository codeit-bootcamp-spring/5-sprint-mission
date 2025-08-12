package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
@ToString
public class ReadStatus implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final UUID id;
    private final UUID userId;
    private final UUID channelId;

    private final Instant createdAt;
    private Instant updatedAt;

    public ReadStatus(UUID userId, UUID channelId) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.channelId = channelId;
        this.createdAt = Instant.now();
        this.updatedAt = createdAt;
    }

    public void update() {
        this.updatedAt = Instant.now();
    }
}
