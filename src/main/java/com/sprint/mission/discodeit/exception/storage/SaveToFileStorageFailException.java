package com.sprint.mission.discodeit.exception.storage;

import java.util.Map;

import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.server.ServerException;

public class SaveToFileStorageFailException extends StorageException {
	public SaveToFileStorageFailException(Map<String, Object> details) {
		super(ErrorCode.SAVE_TO_FILE_STORAGE_FAIL, details);
	}

	public SaveToFileStorageFailException() {
		super(ErrorCode.SAVE_TO_FILE_STORAGE_FAIL);
	}
}
