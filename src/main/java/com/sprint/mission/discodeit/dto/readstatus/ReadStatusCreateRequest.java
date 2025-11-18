package com.sprint.mission.discodeit.dto.readstatus;

import java.time.Instant;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public record ReadStatusCreateRequest(
	@NotNull
	UUID userId,
	@NotNull
	UUID channelId,
	Instant lastReadAt
) {

}
