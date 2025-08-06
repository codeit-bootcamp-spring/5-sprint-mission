package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class ReadStatus extends BaseEntity{

    private final UUID userId;
    private final UUID channelId;
    private Instant lastReadTime;

    public ReadStatus(UUID userId, UUID channelId, Instant lastReadTime) {
        super();
        this.userId = userId;
        this.channelId = channelId;
        this.lastReadTime = lastReadTime;
    }

    public void updateLastReadTime() {
            this.lastReadTime = Instant.now();
            super.updateUpdatedAt();
    }
}
