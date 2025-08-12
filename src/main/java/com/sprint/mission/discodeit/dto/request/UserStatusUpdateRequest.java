package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record UserStatusUpdateRequest(
        @NotNull UUID id,
        boolean loginStatus
) {
}
