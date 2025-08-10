package com.sprint.mission.discodeit.dto.response;

import com.sprint.mission.discodeit.domain.enums.user.Status;

import java.util.UUID;

public record UserResponse(
        UUID id,
        String email,
        String username,
        String globalName,
        Status status
) {
}
