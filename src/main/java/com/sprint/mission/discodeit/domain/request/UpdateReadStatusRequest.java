package com.sprint.mission.discodeit.domain.request;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UpdateReadStatusRequest {
	@NotNull(message = "newLastReadAt는 null이 될 수 없습니다.")
	@PastOrPresent(message = "newLastReadAt는 현재 시점 이하여야합니다.")
	private final Instant newLastReadAt;

	@JsonCreator
	public UpdateReadStatusRequest(@JsonProperty("newLastReadAt") Instant newLastReadAt) {
		this.newLastReadAt = newLastReadAt;
	}
}
