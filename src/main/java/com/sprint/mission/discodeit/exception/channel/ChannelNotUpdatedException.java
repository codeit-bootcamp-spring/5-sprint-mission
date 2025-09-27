package com.sprint.mission.discodeit.exception.channel;

import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.exception.ErrorCode;

public class ChannelNotUpdatedException extends ChannelException {
  public ChannelNotUpdatedException() {super(ErrorCode.PRIVATE_CHANNEL_UPDATE);}

  public static ChannelNotUpdatedException withId(ChannelType channelType) {
    ChannelNotUpdatedException ex = new ChannelNotUpdatedException();
    ex.addDetail("channelType", channelType);
    return ex;
  }

}
