package com.sprint.mission.discodeit.domain.userstatus.dto;

import java.time.Instant;
import java.util.UUID;

public record UserStatusDto(
    UUID id,
    UUID userId,
    Instant lastActiveAt) {

}
