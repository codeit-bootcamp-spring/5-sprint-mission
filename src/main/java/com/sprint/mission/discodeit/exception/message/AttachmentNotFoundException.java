package com.sprint.mission.discodeit.exception.message;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public class AttachmentNotFoundException extends MessageException {

  public AttachmentNotFoundException() {
    super(ErrorCode.ATTACHMENT_NOT_FOUND);
  }

  public AttachmentNotFoundException(Map<String, Object> details) {
    super(ErrorCode.ATTACHMENT_NOT_FOUND, details);
  }
}
