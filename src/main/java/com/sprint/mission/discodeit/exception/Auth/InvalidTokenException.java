package com.sprint.mission.discodeit.exception.Auth;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class InvalidTokenException extends TokenException {

	public InvalidTokenException() {
		super(ErrorCode.INVALID_TOKEN);
	}

	public static InvalidTokenException withRefreshToken(String refreshToken) {
		InvalidTokenException invalidTokenException = new InvalidTokenException();
		invalidTokenException.addDetail("refreshToken", refreshToken);
		return invalidTokenException;
	}

	public static InvalidTokenException withAccessToken(String accessToken) {
		InvalidTokenException invalidTokenException = new InvalidTokenException();
		invalidTokenException.addDetail("accessToken", accessToken);
		return invalidTokenException;
	}

}
