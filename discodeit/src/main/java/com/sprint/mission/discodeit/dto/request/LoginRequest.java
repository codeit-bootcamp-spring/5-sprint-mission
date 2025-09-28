package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotNull;

public record LoginRequest(
    @NotNull String username,
    @NotNull String password
) {

}
