package com.sprint.mission.discodeit.exception.storage;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class StorageReadException extends StorageException {

  public StorageReadException(Throwable cause) {
    super(ErrorCode.STORAGE_READ_FAILED, cause);
  }

  public static StorageReadException withDetail(String key, Object value, Throwable cause) {
    StorageReadException exception = new StorageReadException(cause);
    exception.addDetail(key, value);
    return exception;
  }

}
