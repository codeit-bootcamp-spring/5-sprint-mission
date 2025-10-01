package com.sprint.mission.discodeit.exception.channel;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.UUID;

public class ChannelPrivateUpdateException extends ChannelException {

  public ChannelPrivateUpdateException() {
    super(ErrorCode.PRIVATE_CHANNEL_UPDATE);
  }

  public static ChannelPrivateUpdateException withChannelId(UUID channelId) {
    ChannelPrivateUpdateException exception = new ChannelPrivateUpdateException();
    exception.addDetail("channelId", channelId);
    return exception;
  }
}
