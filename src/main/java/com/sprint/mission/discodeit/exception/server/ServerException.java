package com.sprint.mission.discodeit.exception.server;

import java.util.Map;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;

public class ServerException extends DiscodeitException {

	public ServerException(ErrorCode code, Map<String, Object> details) {
		super(code, details);
	}

	public ServerException(ErrorCode code) {
		super(code);
	}
}
