package com.sprint.mission.discodeit.dto.readstatus;

import lombok.Getter;
import lombok.ToString;

import java.time.Instant;
import java.util.UUID;

@Getter
@ToString
public class ReadStatusCreateRequest {
    private final UUID userId;
    private final UUID channelId;
    private final Instant lastReadAt;

    public ReadStatusCreateRequest(UUID userId, UUID channelId, Instant lastReadAt) {
        this.userId = userId;
        this.channelId = channelId;
        this.lastReadAt = lastReadAt;
    }
}
