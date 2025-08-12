package com.sprint.mission.discodeit.dto.request;

import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record ReadStatusUpdateRequest(
        UUID id,
        boolean read
) {
}
