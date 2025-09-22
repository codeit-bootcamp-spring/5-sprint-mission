package com.sprint.mission.discodeit.exception.file;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;
import java.util.UUID;

public class BinaryContentNotFoundException extends BinaryContentException {
  public BinaryContentNotFoundException(UUID id) { super(ErrorCode.BINARY_CONTENT_NOT_FOUND, Map.of("binaryContentId", id)); }
}
