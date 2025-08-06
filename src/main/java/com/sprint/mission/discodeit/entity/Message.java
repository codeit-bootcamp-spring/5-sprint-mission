package com.sprint.mission.discodeit.entity;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class Message implements Serializable {
	@Serial
	private static final long serialVersionUID = 1L;

	private final UUID id;
	private final Long createdAt;
	private Long updatedAt;
	private UUID authorUUID;
	private UUID channelUUID;
	private String text;

	public Message(UUID authorUUID, UUID channelUUID,String text) {
		this.authorUUID = Objects.requireNonNull(authorUUID, "작성자UUID는 필수 입니다.");
		this.channelUUID = Objects.requireNonNull(channelUUID, "채널UUID는 필수입니다.");
		this.text = Objects.requireNonNull(text, "내용은 필수 입니다.");

		id = UUID.randomUUID();
		createdAt = Instant.now().getEpochSecond();
		updatedAt = createdAt;
	}

	public Message(Message original) {
		this.id = original.id;
		this.createdAt = original.createdAt;
		this.updatedAt = original.updatedAt;
		this.authorUUID = original.authorUUID;
		this.channelUUID = original.channelUUID;
		this.text = original.text;
	}

	public UUID getId() {
		return id;
	}

	public Long getCreatedAt() {
		return createdAt;
	}

	public Long getUpdatedAt() {
		return updatedAt;
	}

	public UUID getAuthorUUID() {
		return authorUUID;
	}

	public String getText() {
		return text;
	}

	public UUID getChannelUUID() {
		return channelUUID;
	}


	public void updateUpdatedAt() {
		this.updatedAt = Instant.now().getEpochSecond();
	}

	public void updateAuthorUUID(UUID authorUUID) {
		this.authorUUID = authorUUID;
	}

	public void updateChannelUUID(UUID channelUUID) {
		this.channelUUID = channelUUID;
	}

	public void updateText(String text) {
		this.text = text;
	}

	public Message copy() {
		return new Message(this);
	}
}
