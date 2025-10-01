package com.sprint.mission.discodeit.exception.auth;

import java.util.Map;

import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.user.UserException;

public class WrongPasswordException extends UserException {
	public WrongPasswordException(Map<String, Object> details) {
		super(ErrorCode.WRONG_PASSWORD, details);
	}

	public WrongPasswordException() {
		super(ErrorCode.WRONG_PASSWORD);
	}
}
