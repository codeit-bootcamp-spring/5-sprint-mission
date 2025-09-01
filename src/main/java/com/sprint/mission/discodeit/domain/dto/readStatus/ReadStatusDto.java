package com.sprint.mission.discodeit.domain.dto.readStatus;

import java.time.Instant;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class ReadStatusDto {
	private UUID id;
	private UUID userId;
	private UUID channelId;
	private Instant lastReadAt;
}
