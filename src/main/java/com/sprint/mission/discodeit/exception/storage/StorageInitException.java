package com.sprint.mission.discodeit.exception.storage;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class StorageInitException extends StorageException {

	public StorageInitException(Throwable cause) {
		super(ErrorCode.STORAGE_INIT_FAILED, cause);
	}
}
