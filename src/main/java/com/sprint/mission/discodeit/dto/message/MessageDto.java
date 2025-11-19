package com.sprint.mission.discodeit.dto.message;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.log.LogUtils;

import lombok.Builder;

@Builder
public record MessageDto(
	UUID id,
	Instant createdAt,
	Instant updatedAt,
	String content,
	UUID channelId,
	UserDto author,
	List<BinaryContentDto> attachments
) {

	public String forLog() {
		return "MessageDto{" +
			"id=" + id +
			", createdAt=" + createdAt +
			", updatedAt=" + updatedAt +
			", content=" + LogUtils.summarize(content, 30) +
			", channelId=" + channelId +
			", authorId=" + author.id() +
			", attachments" + LogUtils.summarizeAttachments(attachments, 3) +
			"}";
	}
}
