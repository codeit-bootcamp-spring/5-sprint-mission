package com.sprint.mission.discodeit.exception.channel;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public class DuplicateChannelException extends ChannelException {

  public DuplicateChannelException() {
    super(ErrorCode.DUPLICATE_CHANNEL);
  }

  public DuplicateChannelException(Map<String, Object> details) {
    super(ErrorCode.DUPLICATE_CHANNEL, details);
  }
}
