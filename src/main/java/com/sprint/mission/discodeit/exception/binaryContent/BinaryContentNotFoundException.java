package com.sprint.mission.discodeit.exception.binaryContent;

import java.util.Map;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class BinaryContentNotFoundException extends BinaryContentException {
	public BinaryContentNotFoundException(Map<String, Object> details) {
		super(ErrorCode.BINARY_CONTENT_NOT_FOUND, details);
	}

	public BinaryContentNotFoundException() {
		super(ErrorCode.BINARY_CONTENT_NOT_FOUND);
	}
}
