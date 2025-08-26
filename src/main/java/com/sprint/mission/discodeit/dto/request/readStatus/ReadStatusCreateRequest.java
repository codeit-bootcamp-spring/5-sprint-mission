package com.sprint.mission.discodeit.dto.request.readStatus;

import java.time.Instant;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ReadStatusCreateRequest {
	@NotNull(message = "사용자 ID는 필수")
	private UUID userId;
	@NotNull(message = "채널 ID는 필수")
	private UUID channelId;
	private Instant lastReadAt;
}
