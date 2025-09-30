package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

/* RunTimeException <- DiscodeitException <- UserException 상속의 관계
 */

public class DuplicateUserException extends UserException {

  public DuplicateUserException() {
    super(ErrorCode.DUPLICATE_USER);
  }

  public DuplicateUserException(Map<String, Object> details) {
    super(ErrorCode.DUPLICATE_USER, details);
  }
}
