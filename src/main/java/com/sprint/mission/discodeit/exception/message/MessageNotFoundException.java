package com.sprint.mission.discodeit.exception.message;

import com.sprint.mission.discodeit.exception.base.DiscodeitException;
import com.sprint.mission.discodeit.exception.base.ErrorCode;

import java.util.Map;

public class MessageNotFoundException extends DiscodeitException {

  public MessageNotFoundException() {
    super(ErrorCode.MESSAGE_NOT_FOUND);
  }

  public MessageNotFoundException(Map<String, Object> details) {
    super(ErrorCode.MESSAGE_NOT_FOUND, details);
  }
}
