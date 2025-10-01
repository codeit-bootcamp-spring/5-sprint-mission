package com.sprint.mission.discodeit.exception.channelException;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class ChannelNotFoundException extends ChannelException {
  public ChannelNotFoundException() {
    super(ErrorCode.CHANNEL_NOT_FOUND);
  }

  public static ChannelNotFoundException withId(String channelId) {
    ChannelNotFoundException exception = new ChannelNotFoundException();
    exception.addDetail("channelId", channelId);
    return exception;
  }

}
