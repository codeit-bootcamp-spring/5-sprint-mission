package com.sprint.mission.discodeit.dto.data;

import com.fasterxml.jackson.annotation.JsonProperty;

public record JwtDto(
	@JsonProperty("userDto")
	UserDto userDto,

	@JsonProperty("accessToken")
	String accessToken
) {
}
