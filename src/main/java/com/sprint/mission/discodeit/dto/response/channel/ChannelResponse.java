package com.sprint.mission.discodeit.dto.response.channel;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.sprint.mission.discodeit.domain.entity.Channel;
import com.sprint.mission.discodeit.domain.enums.ChannelType;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public record ChannelResponse(

    UUID id,

    ChannelType type,

    @JsonInclude(Include.ALWAYS)
    String name,

    @JsonInclude(Include.ALWAYS)
    String description,

    Set<UUID> participantIds,

    @JsonInclude(JsonInclude.Include.ALWAYS)
    Instant lastMessageAt
) {

  public static ChannelResponse from(Channel c, Instant lastMessageAt) {
    return new ChannelResponse(
        c.getId(),
        c.getType(),
        c.getName(),
        c.getDescription(),
        c.getParticipantIds(),
        lastMessageAt
    );
  }
}
