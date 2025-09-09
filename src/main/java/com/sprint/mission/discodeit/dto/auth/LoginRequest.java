package com.sprint.mission.discodeit.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(
    example = """
        {
          "username": "test",
          "password": "P@ssw0rd!"
        }
        """
)
public record LoginRequest(
    @NotBlank String username,
    @NotBlank String password
) {

}
