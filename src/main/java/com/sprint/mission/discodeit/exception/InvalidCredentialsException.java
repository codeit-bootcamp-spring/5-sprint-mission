package com.sprint.mission.discodeit.exception;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;

public class InvalidCredentialsException extends DiscodeitException {
  public InvalidCredentialsException(String username) {
    super(ErrorCode.UNAUTHORIZED, "Invalid credentials for username: " + username);
  }
}
