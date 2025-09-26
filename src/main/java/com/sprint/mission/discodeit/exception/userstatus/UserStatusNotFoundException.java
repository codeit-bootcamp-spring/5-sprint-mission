package com.sprint.mission.discodeit.exception.userstatus;

import com.sprint.mission.discodeit.exception.base.DiscodeitException;
import com.sprint.mission.discodeit.exception.base.ErrorCode;

import java.util.Map;

public class UserStatusNotFoundException extends DiscodeitException {

  public UserStatusNotFoundException() {
    super(ErrorCode.USER_STATUS_NOT_FOUND);
  }

  public UserStatusNotFoundException(Map<String, Object> details) {
    super(ErrorCode.USER_STATUS_NOT_FOUND, details);
  }
}
