package com.sprint.mission.discodeit.exception.binarycontent;

import com.sprint.mission.discodeit.exception.base.DiscodeitException;
import com.sprint.mission.discodeit.exception.base.ErrorCode;

import java.util.Map;

public class BinaryContentNotFoundException extends DiscodeitException {

  public BinaryContentNotFoundException() {
    super(ErrorCode.BINARY_CONTENT_NOT_FOUND);
  }

  public BinaryContentNotFoundException(Map<String, Object> details) {
    super(ErrorCode.BINARY_CONTENT_NOT_FOUND, details);
  }
}
