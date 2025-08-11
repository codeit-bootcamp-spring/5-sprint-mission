package com.sprint.mission.discodeit.dto.channel.data;

import com.sprint.mission.discodeit.entity.enums.ChannelType;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ChannelDto(
        UUID id,
        ChannelType type,
        String name,
        String description,
        List<UUID> participantIds,
        Instant lastMessageAt
) {}