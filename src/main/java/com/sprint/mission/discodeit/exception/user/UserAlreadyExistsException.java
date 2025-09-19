package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class UserAlreadyExistsException extends UserException {

  public UserAlreadyExistsException() {
    super(ErrorCode.DUPLICATE_USER);
  }

  public static UserAlreadyExistsException withDetails(String detail) {
    UserAlreadyExistsException exception = new UserAlreadyExistsException();
    exception.addDetail("detail", detail);
    return exception;
  }

}
