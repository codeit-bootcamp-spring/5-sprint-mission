package com.sprint.mission.discodeit.exception;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;

public class ReadStatusNotFoundException extends DiscodeitException {
  public ReadStatusNotFoundException(Object readStatusId) {
    super(ErrorCode.READ_STATUS_NOT_FOUND, "Read status not found: " + readStatusId);
  }
}
