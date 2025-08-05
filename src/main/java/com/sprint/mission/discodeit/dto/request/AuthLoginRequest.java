package com.sprint.mission.discodeit.dto.request;

import java.util.UUID;

public record AuthLoginRequest(
        UUID userId,
        String username,
        String password
) {
}
