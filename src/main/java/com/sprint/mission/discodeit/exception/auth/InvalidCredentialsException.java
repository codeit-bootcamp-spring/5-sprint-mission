package com.sprint.mission.discodeit.exception.auth;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public class InvalidCredentialsException extends AuthException {
  public InvalidCredentialsException(String usernameOrEmail) {
    super(ErrorCode.AUTH_INVALID_CREDENTIALS, Map.of("usernameOrEmail", usernameOrEmail));
  }
}