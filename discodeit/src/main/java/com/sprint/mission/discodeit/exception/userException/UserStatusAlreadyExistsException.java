package com.sprint.mission.discodeit.exception.userException;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class UserStatusAlreadyExistsException extends UserStatusException {
  public UserStatusAlreadyExistsException() {super(ErrorCode.DUPLICATE_USERSTATE);};

  public static UserStatusAlreadyExistsException withUserId(String userId) {
    UserStatusAlreadyExistsException userStatusAlreadyExistsException = new UserStatusAlreadyExistsException();
    userStatusAlreadyExistsException.addDetail("userId", userId);
    return userStatusAlreadyExistsException;
  }

  public static UserStatusAlreadyExistsException withUserEmail(String email) {
    UserStatusAlreadyExistsException userStatusAlreadyExistsException = new UserStatusAlreadyExistsException();
    userStatusAlreadyExistsException.addDetail("email", email);
    return userStatusAlreadyExistsException;
  }


}
