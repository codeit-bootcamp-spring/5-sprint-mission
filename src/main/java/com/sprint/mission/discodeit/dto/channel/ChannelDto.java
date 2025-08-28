package com.sprint.mission.discodeit.dto.channel;

import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.enums.ChannelType;
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

    public static ChannelDto from(Channel c, Instant lastMessageAt) {
        return new ChannelDto(
            c.getId(),
            c.getType(),
            c.getName(),
            c.getDescription(),
            c.getParticipants().stream().map(UserDto::from).toList(),
            lastMessageAt
        );
    }
}
