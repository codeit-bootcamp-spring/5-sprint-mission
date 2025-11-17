package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.Role;

import java.time.Instant;
import java.util.UUID;

public record UserDto(
        UUID id,
        Instant createdAt,
        Instant updatedAt,
        String username,
        String email,
        Role role,
        UUID profileId,
        Boolean online
) {
}
