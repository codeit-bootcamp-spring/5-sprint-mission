package com.sprint.mission.discodeit.dto.message;

import java.util.UUID;

public record MessageCreateResponse(
        String content,
        UUID channelId,
        UUID authorId
) {
}
