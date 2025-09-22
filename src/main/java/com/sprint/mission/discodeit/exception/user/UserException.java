package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public abstract class UserException extends DiscodeitException {
  protected UserException(ErrorCode code) { super(code); }
  protected UserException(ErrorCode code, Map<String, Object> details) { super(code, details); }
  protected UserException(ErrorCode code, String message, Map<String, Object> details) { super(code, message, details); }
}

