package com.sprint.mission.discodeit.mapper;

import java.time.Instant;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.sprint.mission.discodeit.domain.dto.channel.ChannelDto;
import com.sprint.mission.discodeit.domain.dto.channel.ChannelResponse;
import com.sprint.mission.discodeit.domain.dto.user.UserDto;
import com.sprint.mission.discodeit.domain.entity.Channel;

@Mapper(componentModel = "spring")
public interface ChannelMapper {

	@Mapping(source = "channel.id", target = "id")
	@Mapping(source = "channel.type", target = "type")
	@Mapping(source = "channel.name", target = "name")
	@Mapping(source = "channel.description", target = "description")
	@Mapping(source = "participants", target = "participants")
	@Mapping(source = "lastMessageAt", target = "lastMessageAt")
	ChannelDto toDto(Channel channel, List<UserDto> participants, Instant lastMessageAt);

	ChannelResponse toResponse(ChannelDto dto);
}
