package com.sprint.mission.discodeit.exception.channel;

import com.sprint.mission.discodeit.exception.base.DiscodeitException;
import com.sprint.mission.discodeit.exception.base.ErrorCode;

import java.util.Map;

public class PrivateChannelUpdateException extends DiscodeitException {

  public PrivateChannelUpdateException() {
    super(ErrorCode.PRIVATE_CHANNEL_UPDATE);
  }

  public PrivateChannelUpdateException(Map<String, Object> details) {
    super(ErrorCode.PRIVATE_CHANNEL_UPDATE, details);
  }
}
