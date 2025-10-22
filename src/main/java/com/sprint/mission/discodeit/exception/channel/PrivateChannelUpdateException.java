package com.sprint.mission.discodeit.exception.channel;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class PrivateChannelUpdateException extends ChannelException {

  public PrivateChannelUpdateException() {
    super(ErrorCode.INVALID_CHANNEL_UPDATE);
  }

  public static PrivateChannelUpdateException invalidChannelUpdate() {
    PrivateChannelUpdateException exception = new PrivateChannelUpdateException();
    return exception;
  }
}
