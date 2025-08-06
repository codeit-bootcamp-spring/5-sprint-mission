package com.sprint.mission.discodeit.dto.request;

import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record MessageCreateRequest(
        String content,
        UUID channelId,
        UUID authorId,
        List<UUID> attachmentIds
) {
}
