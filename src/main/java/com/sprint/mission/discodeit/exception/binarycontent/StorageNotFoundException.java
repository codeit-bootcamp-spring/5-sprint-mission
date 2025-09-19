package com.sprint.mission.discodeit.exception.binarycontent;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class StorageNotFoundException extends BinaryContentException {

  public StorageNotFoundException() {
    super(ErrorCode.STORAGE_NOT_FOUND);
  }

  public static StorageNotFoundException withDetail(String key, Object value) {
    StorageNotFoundException exception = new StorageNotFoundException();
    exception.addDetail(key, value);
    return exception;
  }

}
