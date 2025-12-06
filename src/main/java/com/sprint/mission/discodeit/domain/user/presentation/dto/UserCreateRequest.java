package com.sprint.mission.discodeit.domain.user.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

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
    @NotBlank @Size(max = 50) String username,
    @NotBlank @Size(max = 100) String email,
    @NotBlank @Size(max = 50) String password
) {
}
