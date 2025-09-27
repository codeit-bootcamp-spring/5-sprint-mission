package com.sprint.mission.discodeit.exception.auth;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class AuthWrongPassWordException extends AuthException{
  public AuthWrongPassWordException() {super(ErrorCode.AUTH_WRONG_PASSWORD);}

  public static AuthWrongPassWordException withPassword(){
    AuthWrongPassWordException ex = new AuthWrongPassWordException();
    return ex;
  }

}
