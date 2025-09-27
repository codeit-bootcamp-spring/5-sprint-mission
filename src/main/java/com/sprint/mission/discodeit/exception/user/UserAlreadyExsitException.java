package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class UserAlreadyExsitException extends UserException {
  public UserAlreadyExsitException() {super(ErrorCode.DUPLICATE_USER);}

  public static UserAlreadyExsitException withEmail(String email) {
    UserAlreadyExsitException ex = new UserAlreadyExsitException();
    ex.addDetail("email", email);
    return ex;
  }

  public static UserAlreadyExsitException withUserName(String username) {
    UserAlreadyExsitException ex = new UserAlreadyExsitException();
    ex.addDetail("username", username);
    return ex;
  }

}
