package com.sprint.mission.discodeit.exception.binaryContent;

import java.util.Map;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;

public class BinaryContentException extends DiscodeitException {

	public BinaryContentException(ErrorCode code, Map<String, Object> details) {
		super(code, details);
	}

	public BinaryContentException(ErrorCode code) {
		super(code);
	}
}
