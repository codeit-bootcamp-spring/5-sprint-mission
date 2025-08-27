package com.sprint.mission.discodeit.dto.response;

import java.time.Instant;
import java.util.UUID;
import lombok.Builder;

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
