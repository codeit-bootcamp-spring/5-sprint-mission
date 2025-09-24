package com.sprint.mission.discodeit.exception.readStatus;

import java.util.Map;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;

public class ReadStatusException extends DiscodeitException {

	public ReadStatusException(ErrorCode code, Map<String, Object> details) {
		super(code, details);
	}

	public ReadStatusException(ErrorCode code) {
		super(code);
	}
}
