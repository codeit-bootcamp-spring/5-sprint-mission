package com.sprint.mission.discodeit.exception.server;

import java.util.Map;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class InternalServerErrorException extends ServerException {
	public InternalServerErrorException(Map<String, Object> details) {
		super(ErrorCode.INTERNAL_SERVER_ERROR, details);
	}

	public InternalServerErrorException() {
		super(ErrorCode.INTERNAL_SERVER_ERROR);
	}
}
