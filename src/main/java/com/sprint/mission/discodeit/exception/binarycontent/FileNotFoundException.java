package com.sprint.mission.discodeit.exception.binarycontent;

import com.sprint.mission.discodeit.exception.base.DiscodeitException;
import com.sprint.mission.discodeit.exception.base.ErrorCode;

import java.util.Map;

public class FileNotFoundException extends DiscodeitException {

  public FileNotFoundException() {
    super(ErrorCode.FILE_NOT_FOUND);
  }

  public FileNotFoundException(Map<String, Object> details) {
    super(ErrorCode.FILE_NOT_FOUND, details);
  }
}
