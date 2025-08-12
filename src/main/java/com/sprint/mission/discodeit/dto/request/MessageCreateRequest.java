package com.sprint.mission.discodeit.dto.request;

import java.util.*;

public record MessageCreateRequest(
        UUID userId,
        UUID channelId,
        String content,
        List<UUID> attachmentIds
) {
}
