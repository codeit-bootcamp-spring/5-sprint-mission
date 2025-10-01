package com.sprint.mission.discodeit.domain.request;

import java.time.Instant;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class CreateReadStatusRequest {
	@NotNull(message = "userId는 null이 될 수 없습니다.")
	private final UUID userId;
	@NotNull(message = "channelId는 null이 될 수 없습니다.")
	private final UUID channelId;
	@NotNull(message = "lastReadAt는 null이 될 수 없습니다.")
	@PastOrPresent(message = "lastReadAt는 현재 혹은 과거 시간 이어야합니다. ")
	private final Instant lastReadAt;
}
