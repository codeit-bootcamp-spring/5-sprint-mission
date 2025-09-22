package com.sprint.mission.discodeit.exception.channel;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public abstract class ChannelException extends DiscodeitException {
  protected ChannelException(ErrorCode code) { super(code); }
  protected ChannelException(ErrorCode code, Map<String, Object> details) { super(code, details); }
  protected ChannelException(ErrorCode code, String message, Map<String, Object> details) { super(code, message, details); }
}
