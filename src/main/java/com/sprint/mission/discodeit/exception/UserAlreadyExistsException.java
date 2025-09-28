package com.sprint.mission.discodeit.exception;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;

public class UserAlreadyExistsException extends DiscodeitException {
  public UserAlreadyExistsException(String username) {
    super(ErrorCode.USER_ALREADY_EXISTS, "User already exists: " + username);
  }


}
