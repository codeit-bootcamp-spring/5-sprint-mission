package com.sprint.mission.discodeit.dto.readstatus;

import lombok.Getter;
import lombok.ToString;

import java.time.Instant;
import java.util.UUID;

@Getter
@ToString
public class ReadStatusCreateRequest {
    private UUID userId;
    private UUID channelId;
    private Instant lastReadAt;

    public ReadStatusCreateRequest(UUID userId, UUID channelId, Instant lastReadAt) {
        this.userId = userId;
        this.channelId = channelId;
        this.lastReadAt = lastReadAt;
    }
}
