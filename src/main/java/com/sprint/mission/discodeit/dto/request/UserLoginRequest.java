package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record UserLoginRequest(
        @NotBlank String username,
        @NotBlank String password
) {
}
