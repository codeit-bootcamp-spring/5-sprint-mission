package com.sprint.mission.discodeit.exception.userstatus;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class UserStatusAlreadyExistsException extends UserStatusException {

  public UserStatusAlreadyExistsException() {
    super(ErrorCode.DUPLICATE_USER_STATUS);
  }

  public static UserStatusAlreadyExistsException withDetail(String detail) {
    UserStatusAlreadyExistsException exception = new UserStatusAlreadyExistsException();
    exception.addDetail("detail", detail);
    return exception;
  }

}
