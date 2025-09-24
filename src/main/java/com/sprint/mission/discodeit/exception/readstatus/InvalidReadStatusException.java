package com.sprint.mission.discodeit.exception.readstatus;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public class InvalidReadStatusException extends ReadStatusException {

  public InvalidReadStatusException() {
    super(ErrorCode.INVALID_READ_STATUS);
  }

  public InvalidReadStatusException(Map<String, Object> details) {
    super(ErrorCode.INVALID_READ_STATUS, details);
  }
}
