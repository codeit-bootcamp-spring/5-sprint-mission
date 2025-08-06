package com.sprint.mission.discodeit.dto.response.message;

import java.util.UUID;

import com.sprint.mission.discodeit.entity.Message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class DeleteMessageResponse {
	private UUID id;
	private UUID authorId;
	private UUID channelId;
	private boolean success;

	private DeleteMessageResponse(Message message) {
		this.id = message.getId();
		this.authorId = message.getAuthorId();
		this.channelId = message.getChannelId();
		this.success = true;
	}

	public static DeleteMessageResponse success(Message message) {
		return new DeleteMessageResponse(message);
	}
}