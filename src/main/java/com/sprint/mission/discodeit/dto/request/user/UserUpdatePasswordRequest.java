package com.sprint.mission.discodeit.dto.request.user;

import jakarta.validation.constraints.NotBlank;

public record UserUpdatePasswordRequest(
        @NotBlank String password
) {
}
