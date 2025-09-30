package com.sprint.mission.discodeit.exception.userstatus;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public abstract class UserStatusException extends DiscodeitException {
  protected UserStatusException(ErrorCode code) { super(code); }
  protected UserStatusException(ErrorCode code, Map<String, Object> details) { super(code, details); }
  protected UserStatusException(ErrorCode code, String message, Map<String, Object> details) { super(code, message, details); }
}
