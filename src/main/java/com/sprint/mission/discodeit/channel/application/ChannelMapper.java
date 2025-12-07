package com.sprint.mission.discodeit.channel.application;

import com.sprint.mission.discodeit.channel.application.dto.ChannelInfo;
import com.sprint.mission.discodeit.channel.domain.Channel;
import com.sprint.mission.discodeit.channel.presentation.dto.ChannelDto;
import com.sprint.mission.discodeit.user.application.UserMapper;
import com.sprint.mission.discodeit.user.domain.User;
import com.sprint.mission.discodeit.user.presentation.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ChannelMapper {

    private final UserMapper userMapper;

    public ChannelDto toDto(Channel channel, List<User> participants, Instant lastMessageAt) {
        if (channel == null) {
            return null;
        }

        return new ChannelDto(
            channel.getId(),
            channel.getType(),
            channel.getName(),
            channel.getDescription(),
            mapParticipants(participants),
            lastMessageAt
        );
    }

    public ChannelDto toDto(ChannelInfo channelInfo, List<User> participants, Instant lastMessageAt) {
        if (channelInfo == null) {
            return null;
        }

        return new ChannelDto(
            channelInfo.id(),
            channelInfo.type(),
            channelInfo.name(),
            channelInfo.description(),
            mapParticipants(participants),
            lastMessageAt
        );
    }

    private List<UserDto> mapParticipants(List<User> participants) {
        if (participants == null || participants.isEmpty()) {
            return List.of();
        }

        return userMapper.toDtoList(participants);
    }

    public ChannelInfo toChannelInfo(Channel channel) {
        if (channel == null) {
            return null;
        }

        return new ChannelInfo(
            channel.getId(),
            channel.getType(),
            channel.getName(),
            channel.getDescription()
        );
    }
}
