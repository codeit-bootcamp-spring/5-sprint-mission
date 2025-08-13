package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UserUpdatePasswordRequest(
        @NotBlank String password
) {
}
