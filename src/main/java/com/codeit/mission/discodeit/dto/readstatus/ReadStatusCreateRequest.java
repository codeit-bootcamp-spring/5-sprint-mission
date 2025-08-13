package com.codeit.mission.discodeit.dto.readstatus;

import lombok.Getter;
import lombok.ToString;

import java.time.Instant;
import java.util.UUID;

@Getter
@ToString
public class ReadStatusCreateRequest {

    private final Instant lastReadTime;
    private final UUID channelId;
    private final UUID userId;

    public ReadStatusCreateRequest(Instant lastReadTime, UUID channelId, UUID userId) {
        this.lastReadTime = lastReadTime;
        this.channelId = channelId;
        this.userId = userId;
    }

    public ReadStatusCreateRequest(UUID channelId, UUID userId) {
        this(Instant.now(), channelId, userId);
    }
}
