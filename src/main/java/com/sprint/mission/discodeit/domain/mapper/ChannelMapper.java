package com.sprint.mission.discodeit.domain.mapper;

import com.sprint.mission.discodeit.domain.dto.channel.data.ChannelDto;
import com.sprint.mission.discodeit.domain.dto.user.data.UserDto;
import com.sprint.mission.discodeit.domain.entity.Channel;
import com.sprint.mission.discodeit.domain.entity.User;
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
