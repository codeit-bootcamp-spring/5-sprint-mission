package com.sprint.mission.discodeit.exception.channelException;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class ReadStatusNotFoundException extends ReadStatusException {
  public ReadStatusNotFoundException() { super(ErrorCode.READSTATUS_NOT_FOUND); };


  public static ReadStatusNotFoundException withId(String ReadStatusId) {
    ReadStatusNotFoundException exception = new ReadStatusNotFoundException();
    exception.addDetail("ReadStatusId", ReadStatusId);
    return exception;
  }
}
