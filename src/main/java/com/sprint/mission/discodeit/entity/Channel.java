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
	private String name;
	private String description;


	public Channel(String name, String description) {
		this.name = Objects.requireNonNull(name, "채널 이름은 필수 입력값입니다.");
		id = UUID.randomUUID();
		createdAt = Instant.now();
		this.description = description;
	}

	public Channel(List<UUID> userUUIDs) {
		id = UUID.randomUUID();
		type = "PRIVATE";
		name = "private-"+id;
		createdAt = Instant.now();
	}

	public Channel(Channel original) {
		this.id = original.id;
		this.createdAt = original.createdAt;
		this.name = original.name;
		this.updatedAt = original.updatedAt;
		this.type = original.type;
		this.description = original.description;
	}

	public void updateUpdatedAt() {
		this.updatedAt = Instant.now();
	}

	public Channel copy() {
		return new Channel(this);
	}
}
