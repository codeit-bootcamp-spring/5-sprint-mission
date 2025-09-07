package com.sprint.mission.discodeit.dto.channel;

import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.enums.ChannelType;
import lombok.Builder;
import lombok.Value;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Value
@Builder
public class ChannelDto {
    UUID id;
    ChannelType type;
    String name;
    String description;
    List<UserDto> participants;
    Instant lastMessageAt;
}