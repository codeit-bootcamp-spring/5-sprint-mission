package com.sprint.mission.discodeit.dto.request;

import java.time.Instant;

import jakarta.validation.constraints.NotNull;

public record ReadStatusUpdateRequest(
	@NotNull(message = "새로운 마지막 읽은 시간은 필수입니다")
	Instant newLastReadAt
) {

}
