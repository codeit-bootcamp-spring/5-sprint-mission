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
public class ReadStatus implements Serializable {
	@Serial
	private static final long serialVersionUID = 1L;

	private final UUID id;
	private final UUID userId;
	private final UUID channelId;
	private Instant updatedAt;


	public ReadStatus(UUID userId, UUID channelId) {
		this.id = UUID.randomUUID();
		this.userId = userId;
		this.channelId = channelId;
		this.updatedAt = Instant.now();
	}

	public ReadStatus(ReadStatus original) {
		this.id = original.id;
		this.userId = original.userId;
		this.channelId = original.channelId;
		this.updatedAt = original.updatedAt;
	}

	public ReadStatus copy() {
		return new ReadStatus(this);
	}

	public void updateUpdatedAt(){
		this.updatedAt = Instant.now();
	}
}
