package com.sprint.mission.discodeit.exception.userStatus;

import java.util.Map;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;

public class UserStatusException extends DiscodeitException {

	public UserStatusException(ErrorCode code, Map<String, Object> details) {
		super(code, details);
	}

	public UserStatusException(ErrorCode code) {
		super(code);
	}
}
