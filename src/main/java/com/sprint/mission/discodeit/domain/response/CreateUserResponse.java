package com.sprint.mission.discodeit.domain.response;

import java.time.Instant;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class CreateUserResponse {
	private final UUID id;
	private final String username;
	private final String email;
	private final UUID profileId;
	private final Instant createdAt;
	private Instant updatedAt;
}
