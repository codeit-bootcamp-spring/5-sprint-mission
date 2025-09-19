package com.sprint.mission.discodeit.exception.readstatus;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class ReadStatusAlreadyExistsException extends ReadStatusException {

  public ReadStatusAlreadyExistsException() {
    super(ErrorCode.DUPLICATE_READ_STATUS);
  }

  public static ReadStatusAlreadyExistsException withDetail(String key, Object value) {
    ReadStatusAlreadyExistsException exception = new ReadStatusAlreadyExistsException();
    exception.addDetail(key, value);
    return exception;
  }

}
