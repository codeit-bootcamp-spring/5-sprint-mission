package com.sprint.mission.discodeit.dto.request.readstatus;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ReadStatusCreateRequest(
        @NotNull UUID userId,
        @NotNull UUID channelId,
        UUID lastReadMessageId
) {
}
