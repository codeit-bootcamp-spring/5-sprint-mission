package com.sprint.mission.discodeit.entity;

import java.time.Instant;
import java.util.UUID;

public class Message {
	private final UUID id;
	private final Long createdAt;
	private Long updatedAt;
	private UUID authorUUID;
	private UUID channelUUID;
	private String text;

	public Message(UUID authorUUID, UUID channelUUID,String text) {
		this.authorUUID = authorUUID;
		this.channelUUID = channelUUID;
		this.text = text;

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
}
