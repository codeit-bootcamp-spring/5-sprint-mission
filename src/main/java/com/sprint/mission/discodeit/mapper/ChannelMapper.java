package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.channel.ChannelDto;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import java.time.Instant;
import java.util.List;
import org.mapstruct.Context;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
    componentModel = "spring",
    uses = { UserMapper.class, BinaryContentMapper.class }
)
public interface ChannelMapper {

    @Mapping(source = "channel.id", target = "id")
    ChannelDto toDto(
        Channel channel,
        List<User> participants,
        Instant lastMessageAt,
        @Context Instant onlineSince
    );

    @IterableMapping(qualifiedByName = "userToDtoWithSince")
    List<UserDto> mapParticipants(List<User> users, @Context Instant onlineSince);
}
