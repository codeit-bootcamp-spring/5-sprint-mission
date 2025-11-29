package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.channel.data.ChannelDto;
import com.sprint.mission.discodeit.dto.user.data.UserDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.List;

@Mapper(
    componentModel = "spring",
    uses = UserMapper.class
)
public abstract class ChannelMapper {

    @Autowired
    protected UserMapper userMapper;

    @Mapping(target = "participants", expression = "java(mapParticipants(participants))")
    public abstract ChannelDto toDto(Channel channel, List<User> participants, Instant lastMessageAt);

    protected List<UserDto> mapParticipants(List<User> participants) {
        if (participants == null) {
            return List.of();
        }
        return participants.stream()
            .map(userMapper::toDto)
            .toList();
    }
}
