package com.sprint.mission.discodeit.exception.auth;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public abstract class AuthException extends DiscodeitException {
  protected AuthException(ErrorCode code) { super(code); }
  protected AuthException(ErrorCode code, Map<String, Object> details) { super(code, details); }
  protected AuthException(ErrorCode code, String message, Map<String, Object> details) { super(code, message, details); }
}