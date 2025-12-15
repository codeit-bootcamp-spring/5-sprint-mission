package com.sprint.mission.discodeit.channel.application;

import com.sprint.mission.discodeit.channel.application.dto.ChannelInfoDto;
import com.sprint.mission.discodeit.channel.domain.Channel;
import com.sprint.mission.discodeit.channel.presentation.dto.ChannelDto;
import com.sprint.mission.discodeit.user.application.UserMapper;
import com.sprint.mission.discodeit.user.domain.User;
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
            userMapper.toDtoList(participants),
            lastMessageAt
        );
    }

    public ChannelDto toDtoByInfo(ChannelInfoDto channelInfoDto, List<User> participants, Instant lastMessageAt) {
        if (channelInfoDto == null) {
            return null;
        }

        return new ChannelDto(
            channelInfoDto.id(),
            channelInfoDto.type(),
            channelInfoDto.name(),
            channelInfoDto.description(),
            userMapper.toDtoList(participants),
            lastMessageAt
        );
    }

    public ChannelInfoDto toChannelInfo(Channel channel) {
        if (channel == null) {
            return null;
        }

        return new ChannelInfoDto(
            channel.getId(),
            channel.getType(),
            channel.getName(),
            channel.getDescription()
        );
    }
}
