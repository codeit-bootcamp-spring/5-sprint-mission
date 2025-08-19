package com.sprint.mission.discodeit.dto.response;

import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record UserFindResponse(
    UUID id,
    Instant createdAt,
    Instant updatedAt,
    String username,
    String email,
    UUID profileId,
    boolean online
) {

}
