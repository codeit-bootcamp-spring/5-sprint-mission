package com.sprint.mission.discodeit.exception.multipartfile;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;

public class UploadException extends DiscodeitException {

  public UploadException(ErrorCode errorCode) {
    super(errorCode);
  }

  public UploadException(ErrorCode errorCode, Throwable cause) {
    super(errorCode, cause);
  }
}
