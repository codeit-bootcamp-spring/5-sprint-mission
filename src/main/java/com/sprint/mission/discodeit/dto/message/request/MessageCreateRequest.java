package com.sprint.mission.discodeit.dto.message.request;

import java.util.UUID;

public record MessageCreateRequest(
        String content,
        UUID channelId,
        UUID authorId
) {}
