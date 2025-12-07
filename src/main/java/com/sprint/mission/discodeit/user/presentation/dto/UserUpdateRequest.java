package com.sprint.mission.discodeit.user.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

@Schema(
    example = """
        {
          "newUsername": null,
          "newEmail": "test@example.com",
          "newPassword": null
        }
        """
)
public record UserUpdateRequest(
    @Size(max = 50) String newUsername,
    @Size(max = 100) String newEmail,
    @Size(max = 50) String newPassword
) {
}
