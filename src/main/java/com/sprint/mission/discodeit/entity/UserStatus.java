package com.sprint.mission.discodeit.entity;

import java.time.Instant;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class UserStatus {
	private final UUID id;
	private final UUID userId;
	private Instant createdAt;
	private Instant updatedAt;

	public UserStatus(UUID id, UUID userID){
		this.id = id;
		this.userId = userID;
		this.createdAt = Instant.now();
		updatedAt = createdAt;
	}

	public void updateUpdatedAt(){
		this.updatedAt = Instant.now();
	}
}
