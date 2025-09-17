package com.sprint.mission.discodeit.domain.user.exception;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;

public class UserAlreadyExistsException extends UserException {
  private UserAlreadyExistsException(Map<String, Object> details) {
    super(ErrorCode.USER_ALREADY_EXISTS, details);
  }

  public static UserAlreadyExistsException byEmail(String email) {
    return new UserAlreadyExistsException(Map.of("email", email));
  }

  public static UserAlreadyExistsException byUsername(String username) {
    return new UserAlreadyExistsException(Map.of("username", username));
  }
}
