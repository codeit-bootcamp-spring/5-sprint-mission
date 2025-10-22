package com.sprint.mission.discodeit.exception.channel;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class ChannelNotFoundException extends ChannelException {

  public ChannelNotFoundException(String message) {
    super(ErrorCode.CHANNEL_NOT_FOUND);
  }

  public static ChannelNotFoundException withChannelId(String channelId) {
    ChannelNotFoundException exception = new ChannelNotFoundException(channelId);
    exception.addDetail("channelId", channelId);
    return exception;
  }
}
