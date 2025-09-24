package com.sprint.mission.discodeit.exception.storage;

import java.util.Map;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;

public class StorageException extends DiscodeitException {

	public StorageException(ErrorCode code, Map<String, Object> details) {
		super(code, details);
	}

	public StorageException(ErrorCode code) {
		super(code);
	}
}
