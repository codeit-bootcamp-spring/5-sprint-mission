package com.sprint.mission.discodeit.exception;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;

public class UserStatusNotFoundException extends DiscodeitException {
  public UserStatusNotFoundException(Object userStatusId) {
    super(ErrorCode.USER_STATUS_NOT_FOUND, "User status not found: " + userStatusId);
  }
}
