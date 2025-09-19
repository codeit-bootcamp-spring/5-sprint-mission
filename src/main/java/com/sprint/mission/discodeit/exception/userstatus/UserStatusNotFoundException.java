package com.sprint.mission.discodeit.exception.userstatus;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class UserStatusNotFoundException extends UserStatusException {

  public UserStatusNotFoundException() {
    super(ErrorCode.USER_STATUS_NOT_FOUND);
  }

  public static UserStatusNotFoundException withDetail(String detail) {
    UserStatusNotFoundException exception = new UserStatusNotFoundException();
    exception.addDetail("detail", detail);
    return exception;
  }

}
