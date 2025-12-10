package com.sprint.mission.discodeit.auth.presentation.dto;

import com.sprint.mission.discodeit.user.domain.Role;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(
    example = """
        {
          "userId": "957a0ce6-8fde-4397-bb9a-446dcb49578e",
          "newRole": "CHANNEL_MANAGER"
        }
        """
)
public record UserRoleUpdateRequest(
    UUID userId,
    Role newRole
) {
}
