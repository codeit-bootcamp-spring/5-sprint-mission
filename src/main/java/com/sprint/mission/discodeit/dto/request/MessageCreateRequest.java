package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record MessageCreateRequest(
        String content,
        @NotNull UUID channelId,
        @NotNull UUID authorId,
        List<UUID> attachmentIds
) {
}
