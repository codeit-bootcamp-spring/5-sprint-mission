package com.sprint.mission.discodeit.domain.auth.dto.data;

import com.sprint.mission.discodeit.domain.user.entity.Role;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(
    example = """
        {
          "id": "0d5d2d3e-b3d8-48b3-b880-3711bd8c520f",
          "username": "test",
          "role": "USER"
        }
        """
)
public record UserDetailsDto(
    UUID id,
    String username,
    Role role
) {
}
