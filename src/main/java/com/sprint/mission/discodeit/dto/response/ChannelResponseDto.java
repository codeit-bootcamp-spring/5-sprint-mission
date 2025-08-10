package com.sprint.mission.discodeit.dto.response;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public record ChannelResponseDto(
        UUID id,
        ChannelType type,
        String name,
        String description,
        Instant latestMessageTime,
        List<UUID> userIds
) {

    public static ChannelResponseDto fromEntity(Channel channel, Instant latestMessageTime, List<UUID> userIds) {
        return new ChannelResponseDto(
                channel.getId(),
                channel.getType(),
                channel.getName(),
                channel.getDescription(),
                latestMessageTime,
                userIds
        );
    }

    public static ChannelResponseDto fromEntity(Channel channel, Instant latestMessageTime) {
        return new ChannelResponseDto(
                channel.getId(),
                channel.getType(),
                channel.getName(),
                channel.getDescription(),
                latestMessageTime,
                null
        );
    }
}
