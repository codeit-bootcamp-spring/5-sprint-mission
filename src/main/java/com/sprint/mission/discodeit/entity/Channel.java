package com.sprint.mission.discodeit.entity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Channel {
	private final UUID id;
	private final Long createdAt;
	private Long updatedAt;
	private String ChannelName;
	private final List<UUID> channelUsersUUID;
	private final List<UUID> channelMessagesUUID;
	private final Map<UUID, String> userNicknames;

	public Channel(String channelName) {
		ChannelName = channelName;
		channelUsersUUID = new ArrayList<>();
		channelMessagesUUID = new ArrayList<>();
		userNicknames = new ConcurrentHashMap<UUID, String>();

		id = UUID.randomUUID();
		createdAt = Instant.now().getEpochSecond();
		updatedAt = Instant.now().getEpochSecond();
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

	public List<UUID> getChannelUsersUUID() {
		return channelUsersUUID;
	}

	public List<UUID> getChannelMessagesUUID() {
		return channelMessagesUUID;
	}

	public Map<UUID, String> getUserNicknames() {
		return userNicknames;
	}

	public String getUserNickname(UUID userUUID) {
		return userNicknames.get(userUUID);
	}

	public void updateUpdatedAt() {
		this.updatedAt = Instant.now().getEpochSecond();;
	}

	public void updateChannelName(String channelName) {
		ChannelName = channelName;
	}

	public void addUser(UUID userUUID) {
		this.channelUsersUUID.add(userUUID);
	}

	public void addMessage(UUID messageUUID) {
		this.channelMessagesUUID.add(messageUUID);
	}

	public void addNickname(UUID userUUID, String nickname) {
		this.userNicknames.put(userUUID, nickname);
	}

	public void removeUser(UUID userUUID) {
		this.channelUsersUUID.remove(userUUID);
	}

	public void removeMessage(UUID messageUUID) {
		this.channelMessagesUUID.remove(messageUUID);
	}

	public void removeNickname(UUID userUUID) {
		this.userNicknames.remove(userUUID);
	}
}
