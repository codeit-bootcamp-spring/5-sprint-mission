package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.base.DiscodeitException;
import com.sprint.mission.discodeit.exception.base.ErrorCode;

import java.util.Map;

public class UserNotFoundException extends DiscodeitException {

  public UserNotFoundException() {
    super(ErrorCode.USER_NOT_FOUND);
  }

  public UserNotFoundException(Map<String, Object> details) {
    super(ErrorCode.USER_NOT_FOUND, details);
  }
}
