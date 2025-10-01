package com.sprint.mission.discodeit.exception.channelException;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class ReadStatusAlreadyExistsException extends ReadStatusException {
  public ReadStatusAlreadyExistsException() {super(ErrorCode.DUPLICATE_READSTATUS);};
  
  public static ReadStatusAlreadyExistsException withUserIdAndChannelId(String userId,String channelId) {
    ReadStatusAlreadyExistsException exception = new ReadStatusAlreadyExistsException();
    exception.addDetail("userId", userId);
    exception.addDetail("channelId", channelId);
    return exception;
  }

}
