package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class UserNotFoundException extends UserException {

  public UserNotFoundException() {
    super(ErrorCode.USER_NOT_FOUND);
  }

  public static UserNotFoundException withDetails(String detail) {
    UserNotFoundException exception = new UserNotFoundException();
    exception.addDetail("detail", detail);
    return exception;
  }
}
