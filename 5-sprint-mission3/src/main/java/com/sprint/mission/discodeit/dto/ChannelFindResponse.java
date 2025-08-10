package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.ChannelType;
import lombok.Builder;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Builder
public record ChannelFindResponse(
        UUID id,
        String name,
        String description,
        ChannelType type,
        Instant lastReadAt,
        List<UUID> members
) {
}
