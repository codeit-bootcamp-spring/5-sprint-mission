package com.sprint.mission.discodeit.exception.user;

import java.util.Map;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class DuplicateUserNameOrEmailException extends UserException {
	public DuplicateUserNameOrEmailException(Map<String, Object> details) {
		super(ErrorCode.DUPLICATE_USERNAME_OR_EMAIL, details);
	}

	public DuplicateUserNameOrEmailException() {
		super(ErrorCode.DUPLICATE_USERNAME_OR_EMAIL);
	}
}
