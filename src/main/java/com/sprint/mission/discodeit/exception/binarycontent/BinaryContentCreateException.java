package com.sprint.mission.discodeit.exception.binarycontent;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Collections;

public class BinaryContentCreateException extends BinaryContentException {

  public BinaryContentCreateException(String reason) {
    super(ErrorCode.BINARY_CONTENT_NOT_CREATE, Collections.singletonMap("reason", reason));
  }
}
