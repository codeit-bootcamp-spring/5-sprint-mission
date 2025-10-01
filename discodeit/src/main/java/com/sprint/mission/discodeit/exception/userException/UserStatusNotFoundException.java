package com.sprint.mission.discodeit.exception.userException;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class UserStatusNotFoundException extends UserStatusException {
  public UserStatusNotFoundException() {
    super(ErrorCode.USERSTATUS_NOT_FOUND);
  }

  public static UserStatusNotFoundException withUserStatusId(String userStatusId) {
    UserStatusNotFoundException userStatusNotFoundException = new UserStatusNotFoundException();
    userStatusNotFoundException.addDetail("userStatusId", userStatusId);
    return userStatusNotFoundException;
  }

  public static UserStatusNotFoundException withUserId(String userId) {
    UserStatusNotFoundException userStatusNotFoundException = new UserStatusNotFoundException();
    userStatusNotFoundException.addDetail("userId", userId);
    return userStatusNotFoundException;
  }
}
