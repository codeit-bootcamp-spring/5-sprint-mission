package com.sprint.mission.discodeit.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JwtInformation {
	private UserDto userDto;
	private String accessToken;
	private String refreshToken;

	public void rotate(String accessToken, String refreshToken) {
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
	}
}
