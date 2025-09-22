package com.sprint.mission.discodeit.exception.message;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public abstract class MessageException extends DiscodeitException {
  protected MessageException(ErrorCode code) { super(code); }
  protected MessageException(ErrorCode code, Map<String, Object> details) { super(code, details); }
  protected MessageException(ErrorCode code, String message, Map<String, Object> details) { super(code, message, details); }
}
