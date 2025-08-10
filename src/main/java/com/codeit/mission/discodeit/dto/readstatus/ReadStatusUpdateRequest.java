package com.codeit.mission.discodeit.dto.readstatus;

import lombok.Getter;
import lombok.ToString;

import java.time.Instant;
import java.util.UUID;

@Getter
@ToString
public class ReadStatusUpdateRequest {

    private final UUID readStatusId;
    private final Instant lastReadTime;

    public ReadStatusUpdateRequest(UUID readStatusId, Instant lastReadTime) {
        this.readStatusId = readStatusId;
        this.lastReadTime = lastReadTime;
    }

    public ReadStatusUpdateRequest(UUID readStatusId) {
        this(readStatusId, Instant.now());
    }
}
