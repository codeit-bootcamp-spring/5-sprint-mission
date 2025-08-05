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
	private Instant updatedAt;

	public UserStatus(UUID userID){
		this.userId = userID;

		this.id = UUID.randomUUID();
		this.updatedAt = Instant.now();
	}

	public UserStatus(UserStatus original) {
		this.id = original.id;
		this.userId = original.userId;
		this.updatedAt = original.updatedAt;
	}

	public UserStatus copy() {
		return new UserStatus(this);
	}

	public void updateUpdatedAt(){
		this.updatedAt = Instant.now();
	}
}
