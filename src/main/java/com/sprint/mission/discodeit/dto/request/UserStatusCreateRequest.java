package com.sprint.mission.discodeit.dto.request;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public record UserStatusCreateRequest(
	@NotNull
	UUID userId
) {

}
