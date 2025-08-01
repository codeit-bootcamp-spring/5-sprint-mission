package com.sprint.mission.discodeit.entity;

import java.time.Instant;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class ReadStatus {
	private final UUID id;
	private final UUID userId;
	private final UUID channelId;
	private Instant createdAt;
	private Instant updatedAt;


	public ReadStatus(UUID id, UUID userId, UUID channelId) {
		this.id = id;
		this.userId = userId;
		this.channelId = channelId;
		this.createdAt = Instant.now();
		updatedAt = createdAt;
	}

	public void updateUpdatedAt(){
		this.updatedAt = Instant.now();
	}
}
