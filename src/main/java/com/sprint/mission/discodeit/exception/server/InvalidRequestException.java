package com.sprint.mission.discodeit.exception.server;

import java.util.Map;

import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.user.UserException;

public class InvalidRequestException extends ServerException {
	public InvalidRequestException(Map<String, Object> details) {
		super(ErrorCode.INVALID_REQUEST, details);
	}

	public InvalidRequestException() {
		super(ErrorCode.INVALID_REQUEST);
	}
}
