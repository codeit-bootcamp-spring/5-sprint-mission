package com.sprint.mission.discodeit.exception.storage;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class StorageReadException extends StorageException {

	public StorageReadException(Throwable cause) {
		super(ErrorCode.STORAGE_READ_FAILED, cause);
	}
}
