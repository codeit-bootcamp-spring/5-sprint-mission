package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

/* RunTimeException <- DiscodeitException <- UserException 상속의 관계
 */

public class UserNotFoundException extends UserException {

  public UserNotFoundException() {
    super(ErrorCode.USER_NOT_FOUND);
  }

  public UserNotFoundException(Map<String, Object> details) {
    super(ErrorCode.CHANNEL_NOT_FOUND, details);
  }
}
