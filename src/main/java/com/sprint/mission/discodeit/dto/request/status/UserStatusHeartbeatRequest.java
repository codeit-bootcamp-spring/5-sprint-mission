package com.sprint.mission.discodeit.dto.request.status;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record UserStatusHeartbeatRequest(
        @NotNull UUID userId
) {
}
