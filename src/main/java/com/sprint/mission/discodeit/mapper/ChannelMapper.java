package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.channel.data.ChannelDto;
import com.sprint.mission.discodeit.dto.user.data.UserDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
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
        if (participants == null) {
            return List.of();
        }

        return participants.stream()
            .map(userMapper::toDto)
            .toList();
    }
}
