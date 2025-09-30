package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Collections;

public class UserDuplicateException extends UserException {

  public UserDuplicateException(String reason) {
    super(ErrorCode.DUPLICATE_USER, Collections.singletonMap("reason", reason));
  }
}
