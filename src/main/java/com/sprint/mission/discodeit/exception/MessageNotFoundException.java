package com.sprint.mission.discodeit.exception;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;

public class MessageNotFoundException extends DiscodeitException {
  public MessageNotFoundException(Object messageId) {
    super(ErrorCode.MESSAGE_NOT_FOUND, "Message not found: " + messageId);
  }
}
