package com.sprint.mission.discodeit.dto.channel;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.sprint.mission.discodeit.domain.entity.Channel;
import com.sprint.mission.discodeit.domain.enums.ChannelType;
import com.sprint.mission.discodeit.dto.user.UserDto;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ChannelDto(

    UUID id,

    ChannelType type,

    @JsonInclude(Include.ALWAYS)
    String name,

    @JsonInclude(Include.ALWAYS)
    String description,

    List<UserDto> participants,

    @JsonInclude(JsonInclude.Include.ALWAYS)
    Instant lastMessageAt
) {

  public static ChannelDto from(Channel c, List<UserDto> participants, Instant lastMessageAt) {
    return new ChannelDto(
        c.getId(),
        c.getType(),
        c.getName(),
        c.getDescription(),
        participants,
        lastMessageAt
    );
  }
}
