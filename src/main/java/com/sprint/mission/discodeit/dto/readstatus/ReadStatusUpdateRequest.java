package com.sprint.mission.discodeit.dto.readstatus;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class ReadStatusUpdateRequest {
    private UUID id;
    private Instant lastReadAt;

    public ReadStatusUpdateRequest(UUID id, Instant lastReadAt) {
        this.id = id;
        this.lastReadAt = lastReadAt;
    }

}
