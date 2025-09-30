package com.sprint.mission.discodeit.exception.read;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public abstract class ReadStatusException extends DiscodeitException {
  protected ReadStatusException(ErrorCode code) { super(code); }
  protected ReadStatusException(ErrorCode code, Map<String, Object> details) { super(code, details); }
  protected ReadStatusException(ErrorCode code, String message, Map<String, Object> details) { super(code, message, details); }
}
