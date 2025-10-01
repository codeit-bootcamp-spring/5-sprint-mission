package com.sprint.mission.discodeit.exception;

public class BinaryContentNotFoundException extends DiscodeitException {
  public BinaryContentNotFoundException(Object userId) {
    super(ErrorCode.BINARY_CONTENT_NOT_FOUND, "BinaryContent not found: " + userId);
  }
}
