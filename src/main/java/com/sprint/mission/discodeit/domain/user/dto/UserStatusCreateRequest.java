package com.sprint.mission.discodeit.domain.user.dto;

import java.time.Instant;
import java.util.UUID;

public record UserStatusCreateRequest(
    UUID userId,
    Instant lastActiveAt
) {

}
