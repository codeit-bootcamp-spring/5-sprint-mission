package com.sprint.mission.discodeit.exception.storage;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class StorageFileMissingException extends StorageException {

  public StorageFileMissingException() {
    super(ErrorCode.STORAGE_FILE_MISSING);
  }
}
