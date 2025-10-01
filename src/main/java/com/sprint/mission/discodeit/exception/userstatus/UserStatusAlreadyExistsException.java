package com.sprint.mission.discodeit.exception.userstatus;

import com.sprint.mission.discodeit.exception.base.DiscodeitException;
import com.sprint.mission.discodeit.exception.base.ErrorCode;

import java.util.Map;

public class UserStatusAlreadyExistsException extends DiscodeitException {

  public UserStatusAlreadyExistsException() {
    super(ErrorCode.USER_STATUS_ALREADY_EXISTS);
  }

  public UserStatusAlreadyExistsException(Map<String, Object> details) {
    super(ErrorCode.USER_STATUS_ALREADY_EXISTS, details);
  }
}
