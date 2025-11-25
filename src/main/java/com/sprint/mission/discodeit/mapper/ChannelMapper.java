package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.channel.ChannelDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import org.mapstruct.Mapper;

import java.time.Instant;
import java.util.List;

@Mapper(
    componentModel = "spring",
    uses = UserMapper.class
)
public interface ChannelMapper {

    ChannelDto toDto(
        Channel channel,
        List<User> participants,
        Instant lastMessageAt
    );
}
