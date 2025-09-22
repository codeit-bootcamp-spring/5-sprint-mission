package com.sprint.mission.discodeit.exception.binarycontent;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Collections;
import java.util.UUID;

public class BinaryContentNotFoundException extends BinaryContentException {

  public BinaryContentNotFoundException(UUID binaryContentId) {
    super(ErrorCode.BINARY_CONTENT_NOT_FOUND,
        Collections.singletonMap("BinaryContent Id", binaryContentId));
  }
}
