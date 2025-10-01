package com.sprint.mission.discodeit.exception.binaryContent;

import java.util.Map;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class BinaryContentReadFailException extends BinaryContentException {
	public BinaryContentReadFailException(Map<String, Object> details) {
		super(ErrorCode.BINARY_CONTENT_READ_FAIL, details);
	}

	public BinaryContentReadFailException() {
		super(ErrorCode.BINARY_CONTENT_READ_FAIL);
	}
}
