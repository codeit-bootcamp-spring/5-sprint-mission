package com.sprint.mission.discodeit.exception.userStatus;

import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.user.UserException;

public class UserStatusAlreadyExsitException extends UserStatusException {
  public UserStatusAlreadyExsitException() {super(ErrorCode.USERSTATUS_ALREADY_EXIST);}

  public static UserStatusAlreadyExsitException withId(Long userId, Long channelId) {
    UserStatusAlreadyExsitException ex = new UserStatusAlreadyExsitException();
    ex.addDetail("userId", userId);
    ex.addDetail("channelId", channelId);
    return ex;
  }

}
