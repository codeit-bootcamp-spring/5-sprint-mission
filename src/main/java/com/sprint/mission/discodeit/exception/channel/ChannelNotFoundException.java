package com.sprint.mission.discodeit.exception.channel;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class ChannelNotFoundException extends ChannelException {
  public ChannelNotFoundException() {super(ErrorCode.CHANNEL_NOT_FOUND);}

  public static ChannelNotFoundException withId(Long channelId) {
    ChannelNotFoundException ex = new ChannelNotFoundException();
    ex.addDetail("channelId", channelId);
    return ex;
  }

}
