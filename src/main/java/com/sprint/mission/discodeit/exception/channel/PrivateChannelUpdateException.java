package com.sprint.mission.discodeit.exception.channel;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class PrivateChannelUpdateException extends ChannelException {

  public PrivateChannelUpdateException() {
    super(ErrorCode.INVALID_CHANNEL_UPDATE);
  }

  public static PrivateChannelUpdateException withDetail(String key, Object value) {
    PrivateChannelUpdateException exception = new PrivateChannelUpdateException();
    exception.addDetail(key, value);
    return exception;
  }

}
