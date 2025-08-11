package com.sprint.mission.discodeit.dto.response;

import com.sprint.mission.discodeit.entity.ChannelType;

import java.time.Instant;
import java.util.UUID;

public record PublicChannelResponseDto(
        UUID id,
        String name,
        ChannelType type,
        String description,
        Instant lastMessageAt
) implements ChannelResponse {}