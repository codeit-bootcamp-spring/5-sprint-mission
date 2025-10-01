package com.sprint.mission.discodeit.exception;

import java.util.UUID;

public class UserStatusAlreadyExistsException extends DiscodeitException{
  public UserStatusAlreadyExistsException(Object userStatusId) {
    super(ErrorCode.USER_STATUS_ALREADY_EXISTS, "UserStatus already exists: " + userStatusId);
  }
}
