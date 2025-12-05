package com.sprint.mission.discodeit.domain.channel.mapper;

import com.sprint.mission.discodeit.domain.channel.dto.data.ChannelDto;
import com.sprint.mission.discodeit.domain.channel.entity.Channel;
import com.sprint.mission.discodeit.domain.user.dto.UserDto;
import com.sprint.mission.discodeit.domain.user.entity.User;
import com.sprint.mission.discodeit.domain.user.mapper.UserMapper;
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

    private List<UserDto> mapParticipants(List<User> participants) {
        if (participants == null || participants.isEmpty()) {
            return List.of();
        }

        return participants.stream()
            .map(userMapper::toDto)
            .toList();
    }
}
