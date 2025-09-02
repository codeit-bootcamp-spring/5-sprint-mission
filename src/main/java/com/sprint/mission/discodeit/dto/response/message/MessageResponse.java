package com.sprint.mission.discodeit.dto.response.message;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.sprint.mission.discodeit.entity.Message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class MessageResponse {
	private UUID id;
	private Instant createdAt;
	private Instant updatedAt;
	private UUID authorId;
	private UUID channelId;
	private String content;
	private List<UUID> attachmentIds;
	private boolean success;

	private MessageResponse(Message message) {
		this.id = message.getId();
		this.createdAt = message.getCreatedAt();
		this.updatedAt = message.getUpdatedAt();
		this.authorId = message.getAuthor().getId();
		this.channelId = message.getChannel().getId();
		this.content = message.getContent();
		this.attachmentIds = message.getAttachments().stream().map(ma -> ma.getAttachment().getId()).toList();
		this.success = true;
	}

	public static MessageResponse success(Message message) {
		return new MessageResponse(message);
	}
}
