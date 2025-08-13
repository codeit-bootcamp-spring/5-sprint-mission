package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.Set;
import java.util.UUID;

public record MessageUpdateRequest(
        @NotNull UUID senderId,
        String content,
        Set<UUID> attachmentIds
) {
}
