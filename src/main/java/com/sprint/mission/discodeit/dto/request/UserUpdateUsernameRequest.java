package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UserUpdateUsernameRequest(
        @NotBlank String username
) {
}
