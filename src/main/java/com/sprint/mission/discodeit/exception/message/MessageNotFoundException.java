package com.sprint.mission.discodeit.exception.message;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class MessageNotFoundException extends MessageException {
  public MessageNotFoundException() {super(ErrorCode.MESSAGE_NOT_FOUND);}

  public static MessageNotFoundException withId(Long messageId) {
    MessageNotFoundException ex = new MessageNotFoundException();
    ex.addDetail("messageId", messageId);
    return ex;
  }

}
