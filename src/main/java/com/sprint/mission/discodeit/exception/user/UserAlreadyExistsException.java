package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class UserAlreadyExistsException extends UserException {

  public UserAlreadyExistsException() {
    super(ErrorCode.DUPLICATE_USER);
  }

  public static UserAlreadyExistsException withMessage(String message) {
    UserAlreadyExistsException exception = new UserAlreadyExistsException();
    exception.addDetail("message", message);
    return exception;
  }

}
