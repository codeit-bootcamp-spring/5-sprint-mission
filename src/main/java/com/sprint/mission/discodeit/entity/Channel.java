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

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class Channel implements Serializable {
	@Serial
	private static final long serialVersionUID = 1L;

	private final UUID id;
	private final Instant createdAt;
	private String type = "PUBLIC"; // 채널 타입, PUBLIC 또는 PRIVATE
	private Instant updatedAt;
	private String channelName;
	private final List<UUID> memberIds;
	// userUUID
	private final Map<UUID, String> userNicknames;


	public Channel(String channelName) {
		this.channelName = Objects.requireNonNull(channelName, "채널 이름은 필수 입력값입니다.");
		memberIds = new ArrayList<>();
		userNicknames = new ConcurrentHashMap<UUID, String>();

		id = UUID.randomUUID();
		createdAt = Instant.now();
		updatedAt = createdAt;
	}

	public Channel(List<UUID> userUUIDs) {
		memberIds = new ArrayList<>();
		userNicknames = new ConcurrentHashMap<UUID, String>();

		List<UUID> uniqueUserIds = userUUIDs.stream().distinct().toList();

		id = UUID.randomUUID();
		type = "PRIVATE";
		channelName = "private-"+id;
		createdAt = Instant.now();
		updatedAt = createdAt;
		this.memberIds.addAll(uniqueUserIds);
	}

	public Channel(Channel original) {
		this.id = original.id;
		this.createdAt = original.createdAt;
		this.memberIds = new ArrayList<>(original.memberIds);
		this.userNicknames = new HashMap<>(original.userNicknames);
		this.channelName = original.channelName;
		this.updatedAt = original.updatedAt;
		this.type = original.type;
	}

	public String getUserNickname(UUID userUUID) {
		return userNicknames.get(userUUID);
	}

	public void updateUpdatedAt() {
		this.updatedAt = Instant.now();
	}

	public void updateChannelName(String channelName) {
		this.channelName = channelName;
	}

	public void addUser(UUID userUUID) {
		this.memberIds.add(userUUID);
	}


	public void addNickname(UUID userUUID, String nickname) {
		this.userNicknames.put(userUUID, nickname);
	}

	public void removeUser(UUID userUUID) {
		this.memberIds.remove(userUUID);
	}


	public void removeNickname(UUID userUUID) {
		this.userNicknames.remove(userUUID);
	}

	public Channel copy() {
		return new Channel(this);
	}
}
