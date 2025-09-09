package com.sprint.mission.discodeit.domain.dto.userStatus;

import java.time.Instant;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class UserStatusResponse {
	private UUID id;
	private UUID userId;
	private Instant lastActiveAt;
}
