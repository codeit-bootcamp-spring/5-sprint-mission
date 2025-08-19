package com.sprint.mission.discodeit.dto.response;

import java.time.Instant;
import java.util.UUID;

public record UserCreateResponse(
        UUID id,
        String name,
        String email,
        Instant createdAt,
        UUID profileId
) {

}
