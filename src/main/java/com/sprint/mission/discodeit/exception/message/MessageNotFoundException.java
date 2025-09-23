package com.sprint.mission.discodeit.exception.message;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.UUID;

public class MessageNotFoundException extends MessageException {

  public MessageNotFoundException() {
    super(ErrorCode.MESSAGE_NOT_FOUND);
  }

  public static MessageNotFoundException withMessageId(UUID messageID) {
    MessageNotFoundException exception = new MessageNotFoundException();
    exception.addDetail("messageId", messageID);
    return exception;
  }

}
