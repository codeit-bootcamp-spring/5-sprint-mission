package com.sprint.mission.discodeit.exception.auth;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Collections;
import java.util.UUID;

public class InvalidUserStatusException extends AuthException {

  public InvalidUserStatusException(UUID userId) {
    super(ErrorCode.INVALID_USER_STATUS, Collections.singletonMap("userId", userId));
  }
}
