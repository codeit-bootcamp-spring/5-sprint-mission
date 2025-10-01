package com.sprint.mission.discodeit.exception.user;

import java.util.Map;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class InvalidUserParmException extends UserException {
	public InvalidUserParmException(Map<String, Object> details) {
		super(ErrorCode.INVALID_USER_PARAM, details);
	}

	public InvalidUserParmException() {
		super(ErrorCode.INVALID_USER_PARAM);
	}
}
