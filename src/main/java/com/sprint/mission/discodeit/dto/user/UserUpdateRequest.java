package com.sprint.mission.discodeit.dto.user;


import io.swagger.v3.oas.annotations.media.Schema;

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
    String newUsername,
    String newEmail,
    String newPassword
) {

}
