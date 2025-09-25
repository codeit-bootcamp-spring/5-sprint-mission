package com.sprint.mission.discodeit.exception.storage;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;

public class StorageException extends DiscodeitException {

  public StorageException(ErrorCode errorCode) {
    super(errorCode);
  }

  public StorageException(ErrorCode errorCode, Throwable cause) {
    super(errorCode, cause);
  }
}
