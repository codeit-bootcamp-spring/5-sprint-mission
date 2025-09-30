package com.sprint.mission.discodeit.exception.userStatus;

import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.user.UserException;

public class UserStatusNotFoundException extends UserStatusException {
  public UserStatusNotFoundException() {super(ErrorCode.USERSTATUS_NOT_FOUND);}

  public static UserStatusNotFoundException withId(Long userId) {
    UserStatusNotFoundException ex = new UserStatusNotFoundException();
    ex.addDetail("userId", userId);
    return ex;
  }

}
