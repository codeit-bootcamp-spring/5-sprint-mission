package com.sprint.mission.discodeit.dto.message;

import java.util.UUID;

import com.sprint.mission.discodeit.log.LogUtils;

import jakarta.validation.constraints.NotNull;

public record MessageCreateRequest(
	String content,

	@NotNull
	UUID channelId,

	@NotNull
	UUID authorId
) {

	public String forLog() {
		return "MessageCreateRequest{" +
			", content=" + LogUtils.summarize(content, 30) +
			", channelId" + channelId +
			", authorId" + authorId +
			"}";

	}

}
