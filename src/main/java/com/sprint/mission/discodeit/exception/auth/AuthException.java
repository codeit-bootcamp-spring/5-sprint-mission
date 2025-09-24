package com.sprint.mission.discodeit.exception.auth;

import java.util.Map;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;

public class AuthException extends DiscodeitException {

	public AuthException(ErrorCode code, Map<String, Object> details) {
		super(code, details);
	}

	public AuthException(ErrorCode code) {
		super(code);
	}
}
