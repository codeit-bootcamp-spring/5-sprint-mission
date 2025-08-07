package com.sprint.mission.discodeit.dto.request;

import java.util.UUID;

public record ReadStatusUpdateRequest(
        UUID readStatusId,
        UUID userId,
        UUID channelId
) {
}
