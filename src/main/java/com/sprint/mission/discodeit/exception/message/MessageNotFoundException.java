package com.sprint.mission.discodeit.exception.message;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class MessageNotFoundException extends MessageException {

  public MessageNotFoundException() {
    super(ErrorCode.MESSAGE_NOT_FOUND);
  }

  public static MessageNotFoundException withDetail(String key, Object value) {
    MessageNotFoundException exception = new MessageNotFoundException();
    exception.addDetail(key, value);
    return exception;
  }

}
