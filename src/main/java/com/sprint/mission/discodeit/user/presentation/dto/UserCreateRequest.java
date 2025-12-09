package com.sprint.mission.discodeit.user.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import static com.sprint.mission.discodeit.user.domain.User.EMAIL_MAX_LENGTH;
import static com.sprint.mission.discodeit.user.domain.User.RAW_PASSWORD_MAX_LENGTH;
import static com.sprint.mission.discodeit.user.domain.User.USERNAME_MAX_LENGTH;

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
    @NotBlank @Size(max = USERNAME_MAX_LENGTH) String username,
    @NotBlank @Size(max = EMAIL_MAX_LENGTH) String email,
    @NotBlank @Size(max = RAW_PASSWORD_MAX_LENGTH) String password
) {
}
