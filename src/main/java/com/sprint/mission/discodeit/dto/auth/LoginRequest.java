package com.sprint.mission.discodeit.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(
    example = """
        {
          "username": "test",
          "password": "P@ssw0rd!"
        }
        """
)
public record LoginRequest(
    @NotBlank
    @Size(max = 50)
    String username,

    @NotBlank
    @Size(max = 50)
    String password
) {
}
