package com.sprint.mission.discodeit.exception.binarycontent;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class BinaryContentNotFoundException extends BinaryContentException {

  public BinaryContentNotFoundException() {
    super(ErrorCode.BINARY_CONTENT_NOT_FOUND);
  }

  public static BinaryContentNotFoundException withDetail(String key, Object value) {
    BinaryContentNotFoundException exception = new BinaryContentNotFoundException();
    exception.addDetail(key, value);
    return exception;
  }

}
