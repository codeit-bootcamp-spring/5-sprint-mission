package com.sprint.mission.discodeit.dto.user;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

@Schema(
    example = """
        {
          "newUsername": "test",
          "newEmail": "test@example.com",
          "newPassword": "P@ssw0rd!"
        }
        """
)
public record UserUpdateRequest(
    @Size(max = 50)
    String newUsername,

    @Size(max = 100)
    String newEmail,

    @Size(max = 50)
    String newPassword
) {
}
