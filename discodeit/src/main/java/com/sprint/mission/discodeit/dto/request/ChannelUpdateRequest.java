package com.sprint.mission.discodeit.dto.request;

import java.util.UUID;

public record ChannelUpdateRequest(
        UUID   channelId,
        String newName,
        String newDescription
) {
}
