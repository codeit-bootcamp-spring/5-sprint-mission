package com.sprint.mission.discodeit.exception.file;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public abstract class BinaryContentException extends DiscodeitException {
  protected BinaryContentException(ErrorCode code) { super(code); }
  protected BinaryContentException(ErrorCode code, Map<String, Object> details) { super(code, details); }
  protected BinaryContentException(ErrorCode code, String message, Map<String, Object> details) { super(code, message, details); }
}
