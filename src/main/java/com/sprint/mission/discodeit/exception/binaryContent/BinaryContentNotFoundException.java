package com.sprint.mission.discodeit.exception.binaryContent;

import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.channel.ChannelException;

public class BinaryContentNotFoundException extends BinaryContentException {
  public BinaryContentNotFoundException() {super(ErrorCode.BINARY_CONTENT_NOT_FOUND);}

  public static BinaryContentNotFoundException withKey(Long binaryContentId) {
    BinaryContentNotFoundException ex = new BinaryContentNotFoundException();
    ex.addDetail("binaryContentId", binaryContentId);
    return ex;
  }

}
