package com.sprint.mission.discodeit.security.dto;

import com.sprint.mission.discodeit.dto.data.UserDto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JwtInformation {

	private UserDto userDto;
	private String accessToken;
	private String refreshToken;

	public void rotate(String newAccessToken, String newRefreshToken) {
		this.accessToken = newAccessToken;
		this.refreshToken = newRefreshToken;
	}
}
