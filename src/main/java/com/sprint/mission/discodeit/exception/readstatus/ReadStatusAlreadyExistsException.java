package com.sprint.mission.discodeit.exception.readstatus;

import com.sprint.mission.discodeit.exception.base.DiscodeitException;
import com.sprint.mission.discodeit.exception.base.ErrorCode;

import java.util.Map;

public class ReadStatusAlreadyExistsException extends DiscodeitException {

  public ReadStatusAlreadyExistsException() {
    super(ErrorCode.READ_STATUS_ALREADY_EXISTS);
  }

  public ReadStatusAlreadyExistsException(Map<String, Object> details) {
    super(ErrorCode.READ_STATUS_ALREADY_EXISTS, details);
  }
}
