package com.sprint.mission.discodeit.dto.user;

public record JwtDto(
	UserDto userDto,
	String accessToken
) {
}
