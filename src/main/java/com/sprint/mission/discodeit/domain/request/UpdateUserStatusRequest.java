package com.sprint.mission.discodeit.domain.request;

import java.time.Instant;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class UpdateUserStatusRequest {
	@NotNull(message = "newLastActiveAt은 null이 될 수 없습니다.")
	@PastOrPresent(message = "newLastActiveAt는 현재 시점 이하여야합니다.")
	private Instant newLastActiveAt;
}
