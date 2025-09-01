package com.sprint.mission.discodeit.domain.dto.channel;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.sprint.mission.discodeit.domain.dto.user.UserDto;
import com.sprint.mission.discodeit.domain.enums.ChannelType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class ChannelDto {
	private UUID id;
	private ChannelType type;
	private String name;
	private String description;
	private List<UserDto> participants;
	private Instant lastMessageAt;
}
