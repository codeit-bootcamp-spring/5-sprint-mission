package com.sprint.mission.discodeit.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(
    example = """
        {
          "username": "test",
          "email": "test@example.com",
          "password": "P@ssw0rd!"
        }
        """
)
public record UserCreateRequest(
    @NotBlank String username,
    @NotBlank String email,
    @NotBlank String password
) {

}
