package com.sprint.mission.discodeit.mapper;

import java.time.Instant;
import java.util.List;

import org.mapstruct.Mapper;

import com.sprint.mission.discodeit.dto.channel.ChannelDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface ChannelMapper {

	ChannelDto toDto(Channel channel, List<User> participants, Instant lastMessageAt);
}
