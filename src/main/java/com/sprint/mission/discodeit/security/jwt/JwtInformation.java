package com.sprint.mission.discodeit.security.jwt;

import com.sprint.mission.discodeit.domain.dto.user.UserDto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class JwtInformation {
	private final UserDto userDto;
	private final String accessToken;
	private final String refreshToken;

	// todo 구현해야함
	public void rotate(String accessToken, String refreshToken) {
		return;
	}
}
