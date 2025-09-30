package com.sprint.mission.discodeit.domain.dto.message;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.sprint.mission.discodeit.domain.dto.binaryContent.BinaryContentDto;
import com.sprint.mission.discodeit.domain.dto.user.UserDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@Builder
@ToString // todo
public class MessageDto {
	private UUID id;
	private Instant createdAt;
	private Instant updatedAt;
	private String content;
	private UUID channelId;
	private UserDto author;
	private List<BinaryContentDto> attachments;
}
