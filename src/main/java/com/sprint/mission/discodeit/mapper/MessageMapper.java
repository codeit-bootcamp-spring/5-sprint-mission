package com.sprint.mission.discodeit.mapper;

import org.springframework.stereotype.Component;

import com.sprint.mission.discodeit.domain.dto.message.MessageDto;
import com.sprint.mission.discodeit.domain.dto.message.MessageResponse;
import com.sprint.mission.discodeit.domain.entity.Message;
import com.sprint.mission.discodeit.domain.entity.User;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MessageMapper {
	private final BinaryContentMapper binaryContentMapper;
	private final UserMapper userMapper;

	public MessageDto toDto(Message message, User user, boolean isOnline) {
		return MessageDto.builder()
		  .id(message.getId())
		  .createdAt(message.getCreatedAt())
		  .updatedAt(message.getUpdatedAt())
		  .content(message.getContent())
		  .channelId(message.getChannel().getId())
		  .author(userMapper.toDto(message.getUser(), isOnline))
		  .attachments(message.getAttachments().stream().map(binaryContentMapper::toDto).toList())
		  .build();
	}

	public MessageResponse toResponse(MessageDto dto) {
		return MessageResponse.builder()
		  .id(dto.getId())
		  .createdAt(dto.getCreatedAt())
		  .updatedAt(dto.getUpdatedAt())
		  .content(dto.getContent())
		  .channelId(dto.getChannelId())
		  .author(dto.getAuthor())
		  .attachments(dto.getAttachments())
		  .build();
	}

}
