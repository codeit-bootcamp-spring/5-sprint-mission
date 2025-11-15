package com.sprint.mission.discodeit.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.entity.Message;

@Mapper(componentModel = "spring", uses = {UserMapper.class, BinaryContentMapper.class})
public interface MessageMapper {

	@Mapping(target = "channelId", source = "channel.id")
	MessageDto toDto(Message message);
}
