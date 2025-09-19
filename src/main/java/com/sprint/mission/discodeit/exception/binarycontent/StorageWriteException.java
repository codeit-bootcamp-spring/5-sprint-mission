package com.sprint.mission.discodeit.exception.binarycontent;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class StorageWriteException extends BinaryContentException {

  public StorageWriteException(Throwable cause) {
    super(ErrorCode.STORAGE_WRITE_FAILED, cause);
  }

  public static StorageWriteException withDetail(String key, Object value, Throwable cause) {
    StorageWriteException exception = new StorageWriteException(cause);
    exception.addDetail(key, value);
    return exception;
  }
}
