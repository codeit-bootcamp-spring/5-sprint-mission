package com.sprint.mission.discodeit.exception.message;


import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

//메세지 삭제 권한 없음
public class InvalidReadStatusException extends MessageException {

  public InvalidReadStatusException() {
    super(ErrorCode.MESSAGE_DELETE_FORBIDDEN);
  }

  public InvalidReadStatusException(Map<String, Object> details) {
    super(ErrorCode.MESSAGE_DELETE_FORBIDDEN, details);
  }
}
