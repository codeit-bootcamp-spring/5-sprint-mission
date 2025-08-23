package com.sprint.mission.discodeit.dto.response.channel;

import com.sprint.mission.discodeit.domain.entity.Channel;
import com.sprint.mission.discodeit.domain.enums.ChannelType;
import java.time.Instant;
import java.util.UUID;

public record ChannelSaveResponse(

    UUID id,

    Instant createdAt,

    Instant updatedAt,

    ChannelType type,

    String name,

    String description
) {

  public static ChannelSaveResponse from(Channel c) {
    return new ChannelSaveResponse(
        c.getId(),
        c.getCreatedAt(),
        c.getUpdatedAt(),
        c.getType(),
        c.getName(),
        c.getDescription()
    );
  }
}
