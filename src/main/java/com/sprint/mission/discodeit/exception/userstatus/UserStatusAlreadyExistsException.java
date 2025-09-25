package com.sprint.mission.discodeit.exception.userstatus;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class UserStatusAlreadyExistsException extends UserStatusException {

  public UserStatusAlreadyExistsException() {
    super(ErrorCode.DUPLICATE_USER_STATUS);
  }
}
