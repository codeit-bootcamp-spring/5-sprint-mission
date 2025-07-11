package com.sprint.mission.discodeit.entity;

import java.time.Instant;
import java.util.UUID;

public class Message {
	private final UUID id;
	private Long createdAt;
	private Long updatedAt;
	private UUID authorUUID;
	private UUID channelUUID;
	private String message;

	public Message(UUID authorUUID, UUID channelUUID,String message) {
		this.authorUUID = authorUUID;
		this.message = message;

		id = UUID.randomUUID();
		createdAt = Instant.now().getEpochSecond();
		updatedAt = createdAt;
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

	public String getMessage() {
		return message;
	}

	public UUID getChannelUUID() {
		return channelUUID;
	}

	public void updateCreatedAt(Long createdAt) {
		this.createdAt = createdAt;
	}

	public void updateUpdatedAt(Long updatedAt) {
		this.updatedAt = updatedAt;
	}

	public void updateAuthorUUID(UUID authorUUID) {
		this.authorUUID = authorUUID;
	}

	public void updateChannelUUID(UUID channelUUID) {
		this.channelUUID = channelUUID;
	}

	public void updateMessage(String message) {
		this.message = message;
	}
}
