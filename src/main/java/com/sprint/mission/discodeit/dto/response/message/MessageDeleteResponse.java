package com.sprint.mission.discodeit.dto.response.message;

import java.util.UUID;

import com.sprint.mission.discodeit.entity.Message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class MessageDeleteResponse {
	private UUID id;
	private UUID authorId;
	private UUID channelId;
	private boolean success;

	private MessageDeleteResponse(Message message) {
		this.id = message.getId();
		this.authorId = message.getAuthorId();
		this.channelId = message.getChannelId();
		this.success = true;
	}

	public static MessageDeleteResponse success(Message message) {
		return new MessageDeleteResponse(message);
	}
}