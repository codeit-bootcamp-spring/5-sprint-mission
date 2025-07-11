package com.sprint.mission.discodeit.entity;

import java.time.Instant;
import java.util.UUID;

public class Channel {
	private final UUID id;
	private Long createdAt;
	private Long updatedAt;
	private String ChannelName;
	private ROLE[] AccessPermission;
	private UUID[] channelUsersUUID;
	private UUID[] channelMessagesUUID;

	public Channel(String channelName) {
		ChannelName = channelName;

		id = UUID.randomUUID();
		createdAt = Instant.now().getEpochSecond();
		updatedAt = Instant.now().getEpochSecond();
		AccessPermission = new ROLE[] {ROLE.ADMIN, ROLE.USER};
	}

	public Channel(String channelName, ROLE[] accessPermission) {
		AccessPermission = accessPermission;
		ChannelName = channelName;

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

	public String getChannelName() {
		return ChannelName;
	}

	public ROLE[] getAccessPermission() {
		return AccessPermission;
	}

	public UUID[] getChannelUsersUUID() {
		return channelUsersUUID;
	}

	public UUID[] getChannelMessagesUUID() {
		return channelMessagesUUID;
	}

	public void updateCreatedAt(Long createdAt) {
		this.createdAt = createdAt;
	}

	public void updateUpdatedAt(Long updatedAt) {
		this.updatedAt = updatedAt;
	}

	public void updateChannelName(String channelName) {
		ChannelName = channelName;
	}

	public void updateAccessPermission(ROLE[] accessPermission) {
		AccessPermission = accessPermission;
	}

	public void setChannelUsersUUID(UUID[] channelUserslUUID) {
		this.channelUsersUUID = channelUserslUUID;
	}

	public void setChannelMessagesUUID(UUID[] channelMessagesUUID) {
		this.channelMessagesUUID = channelMessagesUUID;
	}

}
