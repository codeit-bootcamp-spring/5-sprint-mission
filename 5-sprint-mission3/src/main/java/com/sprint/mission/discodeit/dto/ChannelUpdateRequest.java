package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.ChannelType;

import java.util.List;
import java.util.UUID;

public record ChannelUpdateRequest(
        UUID channelId,
        String name,
        String description
) {
}
