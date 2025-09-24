package com.sprint.mission.discodeit.exception.userstatus;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Collections;
import java.util.UUID;

public class UserStatusAlreadyExistsException extends UserStatusException {

  public UserStatusAlreadyExistsException(UUID userId) {
    super(ErrorCode.USER_STATUS_EXISTS,
        Collections.singletonMap("userId", userId != null ? userId : "null"));
  }
}
