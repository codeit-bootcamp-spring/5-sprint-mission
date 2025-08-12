package com.sprint.mission.discodeit.dto.channel.response;

import com.sprint.mission.discodeit.entity.ChannelType;

import java.util.UUID;

public record ChannelResponse(
        UUID id,
        ChannelType type,
        String name,
        String topic,
        String description,
        String createdAt,
        String updatedAt
) {
}
