package com.sprint.mission.discodeit.entity;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class UserStatus implements Serializable {
	@Serial
	private static final long serialVersionUID = 1L;

	private final UUID id;
	private final UUID userId;
	private final Instant createdAt;
	private Instant updatedAt;

	//
	private Instant lastActiveAt;

	public UserStatus(UUID userID){
		this.userId = userID;
		this.createdAt = Instant.now();
		this.id = UUID.randomUUID();
	}

	public UserStatus(UserStatus original) {
		this.id = original.id;
		this.userId = original.userId;
		this.createdAt = original.createdAt;
		this.updatedAt = original.updatedAt;
		this.lastActiveAt = original.lastActiveAt;
	}

	public UserStatus copy() {
		return new UserStatus(this);
	}

	public void updateUpdatedAt(){
		this.updatedAt = Instant.now();
	}

	public void updateLastActiveAt(Instant lastActiveAt) {
		boolean anyValueUpdated = false;
		if (lastActiveAt != null && !lastActiveAt.equals(this.lastActiveAt)) {
			this.lastActiveAt = lastActiveAt;
			anyValueUpdated = true;
		}

		if (anyValueUpdated) {
			this.updatedAt = Instant.now();
		}
	}
}
