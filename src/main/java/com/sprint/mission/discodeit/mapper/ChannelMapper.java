package com.sprint.mission.discodeit.mapper;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Component;

import com.sprint.mission.discodeit.domain.dto.channel.ChannelDto;
import com.sprint.mission.discodeit.domain.dto.channel.ChannelResponse;
import com.sprint.mission.discodeit.domain.dto.user.UserDto;
import com.sprint.mission.discodeit.domain.entity.Channel;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ChannelMapper {
	private final MessageRepository messageRepository;
	private final ReadStatusRepository readStatusRepository;
	private final UserMapper userMapper;

	public ChannelDto toDto(Channel channel, List<UserDto> participants, Instant lastMessageAt) {

		return ChannelDto.builder()
		  .id(channel.getId())
		  .type(channel.getType())
		  .name(channel.getName())
		  .description(channel.getDescription())
		  .participants(participants)
		  .lastMessageAt(lastMessageAt)
		  .build();

	}

	public ChannelResponse toResponse(ChannelDto dto) {
		return ChannelResponse.builder()
		  .id(dto.getId())
		  .type(dto.getType())
		  .name(dto.getName())
		  .description(dto.getDescription())
		  .participants(dto.getParticipants())
		  .lastMessageAt(dto.getLastMessageAt())
		  .build();
	}
}
