package com.sprint.mission.discodeit.exception;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;

public class ChannelNotFoundException extends DiscodeitException {
  public ChannelNotFoundException(Object channelId) {
    super(ErrorCode.CHANNEL_NOT_FOUND, "Channel not found: " + channelId);
  }
}
