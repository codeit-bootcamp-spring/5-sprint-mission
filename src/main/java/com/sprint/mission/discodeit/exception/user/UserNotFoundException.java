package com.sprint.mission.discodeit.exception.user;

import java.util.Map;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class UserNotFoundException extends UserException {
	public UserNotFoundException(Map<String, Object> details) {
		super(ErrorCode.USER_NOT_FOUND, details);
	}

	public UserNotFoundException() {
		super(ErrorCode.USER_NOT_FOUND);
	}
}
