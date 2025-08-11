package com.sprint.mission.discodeit.dto.response;

import com.sprint.mission.discodeit.entity.ChannelType;

import java.util.List;
import java.util.UUID;

public record PrivateChannelResponseDto(
        UUID id,
        String name,
        ChannelType type,
        List<UUID> participantIds
) implements ChannelResponse {}
