package com.sprint.mission.discodeit.dto.response;

import java.util.UUID;

public record UserCreateResponse(
        UUID id,
        String username,
        String email
) {
}
