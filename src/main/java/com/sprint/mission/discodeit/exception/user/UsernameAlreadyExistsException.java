package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.base.DiscodeitException;
import com.sprint.mission.discodeit.exception.base.ErrorCode;

import java.util.Map;

public class UsernameAlreadyExistsException extends DiscodeitException {

  public UsernameAlreadyExistsException() {
    super(ErrorCode.USERNAME_ALREADY_EXISTS);
  }

  public UsernameAlreadyExistsException(Map<String, Object> details) {
    super(ErrorCode.USERNAME_ALREADY_EXISTS, details);
  }
}
