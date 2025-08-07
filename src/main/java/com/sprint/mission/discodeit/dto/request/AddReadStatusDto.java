package com.sprint.mission.discodeit.dto.request;

import java.util.UUID;

public record AddReadStatusDto(
        UUID channelId, UUID userId
) {
}
