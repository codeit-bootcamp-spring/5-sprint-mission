package com.sprint.mission.discodeit.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.sprint.mission.discodeit.domain.dto.message.MessageDto;
import com.sprint.mission.discodeit.domain.dto.message.MessageResponse;
import com.sprint.mission.discodeit.domain.dto.user.UserDto;
import com.sprint.mission.discodeit.domain.entity.Message;

@Mapper(componentModel = "spring", uses = {BinaryContentMapper.class, UserMapper.class})
public interface MessageMapper {

	@Mapping(source = "message.id", target = "id")
	@Mapping(source = "message.createdAt", target = "createdAt")
	@Mapping(source = "message.updatedAt", target = "updatedAt")
	@Mapping(source = "message.content", target = "content")
	@Mapping(source = "message.channel.id", target = "channelId")
	@Mapping(source = "author", target = "author")
	@Mapping(source = "message.attachments", target = "attachments")
	MessageDto toDto(Message message, UserDto author);

	public MessageResponse toResponse(MessageDto dto);

}
