package com.sprint.mission.discodeit.dto.channel.data;

import com.sprint.mission.discodeit.dto.user.data.UserDto;
import com.sprint.mission.discodeit.entity.enums.ChannelType;
import com.sprint.mission.discodeit.entity.main.Channel;

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
) {}