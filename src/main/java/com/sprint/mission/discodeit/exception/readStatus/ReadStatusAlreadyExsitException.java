package com.sprint.mission.discodeit.exception.readStatus;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class ReadStatusAlreadyExsitException extends ReadStatusException {
  public ReadStatusAlreadyExsitException() {super(ErrorCode.READSTATUS_ALREADY_EXIST);}

  public static ReadStatusAlreadyExsitException withId(Long userId, Long channelId) {
    ReadStatusAlreadyExsitException ex = new ReadStatusAlreadyExsitException();
    ex.addDetail("userId", userId);
    ex.addDetail("channelId", channelId);
    return ex;
  }

}
