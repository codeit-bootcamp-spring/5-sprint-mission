package com.sprint.mission.discodeit.entity;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class Message implements Serializable {
	@Serial
	private static final long serialVersionUID = 1L;

	private final UUID id;
	private final Instant createdAt;
	private Instant updatedAt;
	private final UUID authorId;
	private final UUID channelId;
	private String content;
	private final List<UUID> attachmentIds;

	public Message(UUID authorId, UUID channelId, String content) {
		this.authorId = Objects.requireNonNull(authorId, "작성자UUID는 필수 입니다.");
		this.channelId = Objects.requireNonNull(channelId, "채널UUID는 필수입니다.");
		this.content = Objects.requireNonNull(content, "내용은 필수 입니다.");

		id = UUID.randomUUID();
		createdAt = Instant.now();
		updatedAt = createdAt;
		attachmentIds = new ArrayList<>();
	}

	public Message(Message original) {
		this.id = original.id;
		this.createdAt = original.createdAt;
		this.updatedAt = original.updatedAt;
		this.authorId = original.authorId;
		this.channelId = original.channelId;
		this.content = original.content;
		this.attachmentIds = original.attachmentIds;
	}

	public void updateUpdatedAt() {
		this.updatedAt = Instant.now();
	}

	public void addAttachment(UUID attachmentId) {
		if (attachmentId != null && !this.attachmentIds.contains(attachmentId)) {
			this.attachmentIds.add(attachmentId);
		}
	}

	public void removeAttachment(UUID attachmentId) {
		if (this.attachmentIds.remove(attachmentId)) {
			updateUpdatedAt();
		}
	}

	public Message copy() {
		return new Message(this);
	}
}
