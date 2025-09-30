package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class UserNotFoundException extends UserException {
  public UserNotFoundException() {super(ErrorCode.USER_NOT_FOUND);}

  public static UserNotFoundException withId(Long userId) {
    UserNotFoundException ex = new UserNotFoundException();
    ex.addDetail("userId", userId);
    return ex;
  }

}
