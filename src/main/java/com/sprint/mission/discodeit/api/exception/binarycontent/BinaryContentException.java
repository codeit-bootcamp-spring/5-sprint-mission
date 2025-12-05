package com.sprint.mission.discodeit.api.exception.binarycontent;

import com.sprint.mission.discodeit.api.exception.DiscodeitException;
import com.sprint.mission.discodeit.api.exception.ErrorCode;

import java.util.Map;

public class BinaryContentException extends DiscodeitException {

    public BinaryContentException(ErrorCode errorCode) {
        super(errorCode);
    }

    public BinaryContentException(ErrorCode errorCode, Map<String, Object> details) {
        super(errorCode, details);
    }

    public BinaryContentException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
