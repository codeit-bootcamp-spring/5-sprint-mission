package com.sprint.mission.discodeit.exception.storage;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class StorageWriteException extends StorageException {

	public StorageWriteException(Throwable cause) {
		super(ErrorCode.STORAGE_WRITE_FAILED, cause);
	}
}
