package com.sprint.mission.discodeit.exception.channel;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class ChannelAlreadyExistsException extends ChannelException {


  public ChannelAlreadyExistsException(ErrorCode errorCode) {
    super(errorCode);
  }
}
