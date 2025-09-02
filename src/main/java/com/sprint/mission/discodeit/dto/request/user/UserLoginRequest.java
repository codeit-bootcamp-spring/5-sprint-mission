package com.sprint.mission.discodeit.dto.request.user;

import jakarta.validation.constraints.NotBlank;

public record UserLoginRequest(
        @NotBlank
        String username,
        @NotBlank
        String password
) {
}