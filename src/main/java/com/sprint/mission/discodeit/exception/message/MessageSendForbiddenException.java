package com.sprint.mission.discodeit.exception.message;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

//메세지 전송 권한 없음
public class MessageSendForbiddenException extends MessageException {

  public MessageSendForbiddenException() {
    super(ErrorCode.MESSAGE_SEND_FORBIDDEN);
  }

  public MessageSendForbiddenException(Map<String, Object> details) {
    super(ErrorCode.MESSAGE_SEND_FORBIDDEN, details);
  }
}
