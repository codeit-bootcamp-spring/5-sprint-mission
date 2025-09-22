package com.sprint.mission.discodeit.exception.auth;

import com.sprint.mission.discodeit.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public class LoginFailedException extends AuthException {

  public LoginFailedException() {
    super(ErrorCode.LOGIN_FAIL);
  }
}