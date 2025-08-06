package com.sprint.mission.discodeit.entity;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Channel implements Serializable {
	@Serial
	private static final long serialVersionUID = 1L;

	private final UUID id;
	private final Long createdAt;
	private Long updatedAt;
	private String channelName;
	private final List<UUID> channelUsersUUID;
	private final List<UUID> channelMessagesUUID;
	// userUUID
	private final Map<UUID, String> userNicknames;

	public Channel(String channelName) {
		this.channelName = Objects.requireNonNull(channelName, "채널 이름은 필수 입력값입니다.");
		channelUsersUUID = new ArrayList<>();
		channelMessagesUUID = new ArrayList<>();
		userNicknames = new ConcurrentHashMap<UUID, String>();

		id = UUID.randomUUID();
		createdAt = Instant.now().getEpochSecond();
		updatedAt = createdAt;
	}

	public Channel(Channel original) {
		this.id = original.id;
		this.createdAt = original.createdAt;
		this.channelUsersUUID = new ArrayList<>(original.channelUsersUUID);
		this.channelMessagesUUID = new ArrayList<>(original.channelMessagesUUID);
		this.userNicknames = new HashMap<>(original.userNicknames);
		this.channelName = original.channelName;
		this.updatedAt = original.updatedAt;
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
		return channelName;
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
		this.channelName = channelName;
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

	public Channel copy() {
		return new Channel(this);
	}
}
