package com.sprint.mission.discodeit.exception;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;

public class UserNotFoundException extends DiscodeitException {
  public UserNotFoundException(Object userId) {
    super(ErrorCode.USER_NOT_FOUND, "User not found: " + userId);
  }
}
