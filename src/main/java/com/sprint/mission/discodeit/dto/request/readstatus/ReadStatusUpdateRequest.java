package com.sprint.mission.discodeit.dto.request.readstatus;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ReadStatusUpdateRequest(
        @NotNull UUID lastReadMessageId
) {
}
