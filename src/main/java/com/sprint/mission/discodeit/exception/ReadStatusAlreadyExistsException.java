package com.sprint.mission.discodeit.exception;

import java.util.UUID;

public class ReadStatusAlreadyExistsException extends DiscodeitException{
  public ReadStatusAlreadyExistsException(UUID userId, UUID channelId) {
    super(ErrorCode.READ_STATUS_ALREADY_EXISTS, "ReadStatus already exists: userId = " + userId + ", channelId = " + channelId);
  }
}
