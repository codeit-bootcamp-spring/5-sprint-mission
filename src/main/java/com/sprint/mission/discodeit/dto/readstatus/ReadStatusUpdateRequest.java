package com.sprint.mission.discodeit.dto.readstatus;

import lombok.Getter;
import lombok.ToString;

import java.time.Instant;
import java.util.UUID;

@Getter
@ToString
public class ReadStatusUpdateRequest {
    private final UUID id;
    private final Instant lastReadAt;

    public ReadStatusUpdateRequest(UUID id, Instant lastReadAt) {
        this.id = id;
        this.lastReadAt = lastReadAt;
    }
}
