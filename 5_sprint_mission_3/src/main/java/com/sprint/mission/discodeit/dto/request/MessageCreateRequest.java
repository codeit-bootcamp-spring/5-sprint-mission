package com.sprint.mission.discodeit.dto.request;

import java.util.List;
import java.util.UUID;

public record MessageCreateRequest(
        String content,
        UUID channelId,
        UUID userId,
        List<BinaryContentCreateRequest> attachments
) {}
