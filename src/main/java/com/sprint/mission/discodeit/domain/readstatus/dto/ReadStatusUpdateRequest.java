package com.sprint.mission.discodeit.domain.readstatus.dto;

import java.time.Instant;

public record ReadStatusUpdateRequest(
    Instant newLastReadAt
) {

}
