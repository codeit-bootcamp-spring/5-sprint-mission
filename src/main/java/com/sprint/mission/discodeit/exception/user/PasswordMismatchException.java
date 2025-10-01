package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.base.DiscodeitException;
import com.sprint.mission.discodeit.exception.base.ErrorCode;

import java.util.Map;

public class PasswordMismatchException extends DiscodeitException {

  public PasswordMismatchException() {
    super(ErrorCode.PASSWORD_MISMATCH);
  }

  public PasswordMismatchException(Map<String, Object> details) {
    super(ErrorCode.PASSWORD_MISMATCH, details);
  }
}
