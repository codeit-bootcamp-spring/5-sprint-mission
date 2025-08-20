package com.sprint.mission.discodeit.dto.request.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserUpdateEmailRequest(

        @NotBlank
        @Size(min = 6, max = 254)
        @Email
        String email
) {
}
