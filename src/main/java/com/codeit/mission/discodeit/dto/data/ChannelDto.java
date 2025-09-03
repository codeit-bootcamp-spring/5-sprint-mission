package com.codeit.mission.discodeit.dto.data;

import com.codeit.mission.discodeit.entity.ChannelType;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ChannelDto(
    UUID id,
    ChannelType type,
    String name,
    String description,
    List<UserDto> participants,
    Instant lastMessageAt
) {

}
