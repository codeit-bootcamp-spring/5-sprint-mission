package com.sprint.mission.discodeit.exception.channelException;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class PrivateChannelUpdateException extends ChannelException {
  public PrivateChannelUpdateException() {
    super(ErrorCode.PRIVATE_CHANNEL_UPDATE);
  }

  public static PrivateChannelUpdateException cannotUpdate(String  channelId) {
    PrivateChannelUpdateException exception = new PrivateChannelUpdateException();
    exception.addDetail("channelId", channelId);
    return exception;
  }

}
