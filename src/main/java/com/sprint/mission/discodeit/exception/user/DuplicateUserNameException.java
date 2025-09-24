package com.sprint.mission.discodeit.exception.user;

import java.util.Map;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class DuplicateUserNameException extends UserException {
	public DuplicateUserNameException(Map<String, Object> details) {
		super(ErrorCode.DUPLICATE_USERNAME, details);
	}

	public DuplicateUserNameException() {
		super(ErrorCode.DUPLICATE_USERNAME);
	}
}
