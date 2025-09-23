package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class UserWrongException extends UserException {

  public UserWrongException() {
    super(ErrorCode.WRONG_PASSWORD);
  }

  public static UserWrongException wrongPassword() {
    UserWrongException exception = new UserWrongException();
    return exception;
  }
}
