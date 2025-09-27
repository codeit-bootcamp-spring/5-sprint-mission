package com.sprint.mission.discodeit.exception.binaryContent;

import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.readStatus.ReadStatusException;

public class BinaryContentAlreadyExsitException extends ReadStatusException {
  public BinaryContentAlreadyExsitException() {super(ErrorCode.BINARY_CONTENT_ALREADY_EXIST);}

  public static BinaryContentAlreadyExsitException withkey(Long binaryContentId) {
    BinaryContentAlreadyExsitException ex = new BinaryContentAlreadyExsitException();
    ex.addDetail("binaryContentId", binaryContentId);
    return ex;
  }

}
