package com.sprint.mission.discodeit.dto.response;

import com.sprint.mission.discodeit.entity.ChannelType;

import java.time.Instant;
import java.util.*;

public record ChannelFindResponse(
        UUID channelId,
        ChannelType type,
        String name,
        String description,
        Instant updatedAt
) {
}
