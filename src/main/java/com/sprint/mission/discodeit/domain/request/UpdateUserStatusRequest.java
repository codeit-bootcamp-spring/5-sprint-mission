package com.sprint.mission.discodeit.domain.request;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UpdateUserStatusRequest {
	@NotNull(message = "newLastActiveAt은 null이 될 수 없습니다.")
	@PastOrPresent(message = "newLastActiveAt는 현재 시점 이하여야합니다.")
	private Instant newLastActiveAt;

	@JsonCreator
	public UpdateUserStatusRequest(@JsonProperty("newLastActiveAt") Instant newLastActiveAt) {
		this.newLastActiveAt = newLastActiveAt;
	}
}
