package com.sprint.mission.discodeit.exception.userStatus;

import java.util.Map;

import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.user.UserException;

public class UserStatusNotFoundException extends UserException {
	public UserStatusNotFoundException(Map<String, Object> details) {
		super(ErrorCode.USER_STATUS_NOT_FOUND, details);
	}

	public UserStatusNotFoundException() {
		super(ErrorCode.USER_STATUS_NOT_FOUND);
	}
}
