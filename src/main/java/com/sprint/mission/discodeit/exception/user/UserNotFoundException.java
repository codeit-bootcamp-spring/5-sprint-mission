package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Collections;
import java.util.UUID;

public class UserNotFoundException extends DiscodeitException {

  public UserNotFoundException(UUID userId) {
    super(ErrorCode.USER_NOT_FOUND, Collections.singletonMap("User Id", userId));
  }
}
