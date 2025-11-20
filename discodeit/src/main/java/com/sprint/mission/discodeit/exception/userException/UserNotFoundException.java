package com.sprint.mission.discodeit.exception.userException;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class UserNotFoundException extends UserException {

  public UserNotFoundException() {
    super(ErrorCode.USER_NOT_FOUND);
  }

  public static UserNotFoundException withUsername(String username) {
    UserNotFoundException exception = new UserNotFoundException();
    exception.addDetail("username", username);
    return exception;
  }


  public static UserNotFoundException withId(String userId) {
    UserNotFoundException exception = new UserNotFoundException();
    exception.addDetail("userId", userId);
    return exception;
  }

  public static UserNotFoundException withEmail(String email) {
    UserNotFoundException exception = new UserNotFoundException();
    exception.addDetail("email", email);
    return exception;
  }

}
