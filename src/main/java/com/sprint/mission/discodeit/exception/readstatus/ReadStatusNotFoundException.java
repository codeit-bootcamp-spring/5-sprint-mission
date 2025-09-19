package com.sprint.mission.discodeit.exception.readstatus;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class ReadStatusNotFoundException extends ReadStatusException {

  public ReadStatusNotFoundException() {
    super(ErrorCode.READ_STATUS_NOT_FOUND);
  }

  public static ReadStatusNotFoundException withDetail(String key, Object value) {
    ReadStatusNotFoundException exception = new ReadStatusNotFoundException();
    exception.addDetail(key, value);
    return exception;
  }

}
