package com.sprint.mission.discodeit.dto.request.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AuthLoginRequest(

        @Email
        String email,

        @NotBlank
        @Size(min = 8, max = 72)
        String password
) {
}
