package com.sprint.mission.discodeit.dto.channel;

import com.sprint.mission.discodeit.entity.ChannelAccessibility;

import java.util.List;
import java.util.UUID;

public record CreateChannelRequest(
        ChannelAccessibility accessibility,
        String name,
        String description,
        List<UUID> userIdList
) {}
