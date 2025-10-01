package com.sprint.mission.discodeit.exception.user;

import java.util.Map;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class DuplicateUserEmailException extends UserException {
	public DuplicateUserEmailException(Map<String, Object> details) {
		super(ErrorCode.DUPLICATE_USER_EMAIL, details);
	}

	public DuplicateUserEmailException() {
		super(ErrorCode.DUPLICATE_USER_EMAIL);
	}
}
