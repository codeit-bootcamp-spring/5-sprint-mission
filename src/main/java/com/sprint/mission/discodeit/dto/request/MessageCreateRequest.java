package com.sprint.mission.discodeit.dto.request;

import java.util.UUID;

public record MessageCreateRequest(
        UUID channelId,
        UUID authorId,
        String content
) {
}
