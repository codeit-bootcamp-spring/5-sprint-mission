package com.sprint.mission.discodeit.dto;

import java.util.UUID;

public record UserRequest(
        UUID id,
        String username,
        String email,
        String password,
        UUID profileId
) {
}
