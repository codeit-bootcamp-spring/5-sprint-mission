package com.sprint.mission.discodeit.mapper;

import org.springframework.stereotype.Component;

import com.sprint.mission.discodeit.domain.dto.message.MessageDto;
import com.sprint.mission.discodeit.domain.entity.Message;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MessageMapper {
	private final BinaryContentMapper binaryContentMapper;
	private final UserMapper userMapper;

	public MessageDto toDto(Message message) {
		return MessageDto.builder()
		  .id(message.getId())
		  .createdAt(message.getCreatedAt())
		  .updatedAt(message.getUpdatedAt())
		  .content(message.getContent())
		  .channelId(message.getChannel().getId())
		  .author(userMapper.toDto(message.getUser()))
		  .attachments(message.getAttachments().stream().map(binaryContentMapper::toDto).toList())
		  .build();
	}
}
