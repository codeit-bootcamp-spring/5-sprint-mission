package com.sprint.mission.discodeit.exception.binarycontent;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.UUID;

public class BinaryContentNotFoundException extends BinaryContentException {

  public BinaryContentNotFoundException() {
    super(ErrorCode.BINARYCONTENT_NOT_FOUND);
  }

  public static BinaryContentNotFoundException withId(UUID id) {
    BinaryContentNotFoundException exception = new BinaryContentNotFoundException();
    exception.addDetail("id", id);
    return exception;
  }
}
