package com.sprint.mission.discodeit.exception.user;

import java.util.Map;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class InvalidUserCredentialsException extends UserException {
	public InvalidUserCredentialsException(Map<String, Object> details) {
		super(ErrorCode.INVALID_USER_CREDENTIALS, details);
	}

	public InvalidUserCredentialsException() {
		super(ErrorCode.INVALID_USER_CREDENTIALS);
	}
}
