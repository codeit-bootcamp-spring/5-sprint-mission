package com.sprint.mission.discodeit.exception.binarycontent;

import com.sprint.mission.discodeit.exception.base.DiscodeitException;
import com.sprint.mission.discodeit.exception.base.ErrorCode;

import java.util.Map;

public class FileAlreadyExistsException extends DiscodeitException {

  public FileAlreadyExistsException() {
    super(ErrorCode.FILE_ALREADY_EXISTS);
  }

  public FileAlreadyExistsException(Map<String, Object> details) {
    super(ErrorCode.FILE_ALREADY_EXISTS, details);
  }
}
