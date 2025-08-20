package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class ReadStatus extends BaseEntity {
    private final UUID userId;
    private final UUID channelId;
    private Instant lastReadAt;

    public ReadStatus(UUID userId, UUID channelId, Instant lastReadAt) {
        this(UUID.randomUUID(), Instant.now(), userId, channelId, lastReadAt);
    }

    public ReadStatus(UUID id, Instant createAt, UUID userId, UUID channelId, Instant lastReadAt) {
        super(id, createAt);
        this.userId = userId;
        this.channelId = channelId;
        this.lastReadAt = lastReadAt;
    }

    public void updateLastReadAt(Instant lastReadAt) {
        this.lastReadAt = lastReadAt;
        updateTimeStamp();
    }
}
