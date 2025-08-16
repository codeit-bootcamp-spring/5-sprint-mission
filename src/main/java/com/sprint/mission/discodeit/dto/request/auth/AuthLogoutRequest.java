package com.sprint.mission.discodeit.dto.request.auth;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AuthLogoutRequest(
        @NotNull UUID userId
) {
}
