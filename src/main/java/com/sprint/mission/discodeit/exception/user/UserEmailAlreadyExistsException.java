package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

//이미 가입된 이메일인지
public class UserEmailAlreadyExistsException extends UserException {

  public UserEmailAlreadyExistsException() {
    super(ErrorCode.USER_EMAIL_ALREADY_EXISTS);
  }

  public UserEmailAlreadyExistsException(Map<String, Object> details) {
    super(ErrorCode.USER_EMAIL_ALREADY_EXISTS, details);
  }
}
