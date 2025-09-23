package com.sprint.mission.discodeit.exception.readstatus;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.time.Instant;

public class ReadStatusAlreadyExistsException extends ReadStatusException {

  public ReadStatusAlreadyExistsException() {
    super(ErrorCode.DUPLICATE_READ_STATUS);
  }

  public static ReadStatusAlreadyExistsException withUserIdAndChannelId(String userId,
      String channelId) {
    ReadStatusAlreadyExistsException exception = new ReadStatusAlreadyExistsException();
    exception.addDetail("userIdAndChannelId", userId + "&" + channelId);
    return exception;
  }

}
