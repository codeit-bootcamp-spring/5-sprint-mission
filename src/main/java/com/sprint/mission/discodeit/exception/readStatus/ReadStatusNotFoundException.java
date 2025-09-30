package com.sprint.mission.discodeit.exception.readStatus;

import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.userStatus.UserStatusException;

public class ReadStatusNotFoundException extends ReadStatusException {
  public ReadStatusNotFoundException() {super(ErrorCode.READSTATUS_NOT_FOUND);}

  public static ReadStatusNotFoundException withId(Long readStatusId) {
    ReadStatusNotFoundException ex = new ReadStatusNotFoundException();
    ex.addDetail("readStatusId", readStatusId);
    return ex;
  }

}
