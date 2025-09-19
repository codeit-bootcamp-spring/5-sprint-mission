package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class UserNotFoundException extends UserException {

  public UserNotFoundException() {
    super(ErrorCode.USER_NOT_FOUND);
  }

  public static UserNotFoundException withDetail(String key, Object value) {
    UserNotFoundException exception = new UserNotFoundException();
    exception.addDetail(key, value);
    return exception;
  }
}
