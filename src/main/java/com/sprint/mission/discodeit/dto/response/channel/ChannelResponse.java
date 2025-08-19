package com.sprint.mission.discodeit.dto.response.channel;

import com.sprint.mission.discodeit.domain.entity.Channel;
import com.sprint.mission.discodeit.domain.enums.ChannelType;
import java.time.Instant;
import java.util.UUID;

public record ChannelResponse(
    UUID id,
    Instant createdAt,
    Instant updatedAt,
    ChannelType type,
    String name,
    String description
) {

  public static ChannelResponse from(Channel c) {
    return new ChannelResponse(
        c.getId(),
        c.getCreatedAt(),
        c.getUpdatedAt(),
        c.getType(),
        c.getName(),
        c.getDescription()
    );
  }
}
