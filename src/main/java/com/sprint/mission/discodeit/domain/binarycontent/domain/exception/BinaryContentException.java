package com.sprint.mission.discodeit.domain.binarycontent.domain.exception;

import com.sprint.mission.discodeit.global.error.ErrorCode;
import com.sprint.mission.discodeit.global.exception.DiscodeitException;

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
