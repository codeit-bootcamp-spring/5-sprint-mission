package com.sprint.mission.discodeit.dto.auth.request;

import com.sprint.mission.discodeit.entity.Role;
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
public record RoleUpdateRequest(UUID userId, Role newRole) {
}
