package com.sprint.mission.discodeit.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record AuthLoginRequest(
    @NotBlank
    String username,
    @NotBlank
    String password
) {

}
