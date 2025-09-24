package com.sprint.mission.discodeit.exception.server;

import java.util.Map;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class UriCreateFailException extends ServerException {
	public UriCreateFailException(Map<String, Object> details) {
		super(ErrorCode.URI_CREATE_FAIL, details);
	}

	public UriCreateFailException() {
		super(ErrorCode.URI_CREATE_FAIL);
	}
}
