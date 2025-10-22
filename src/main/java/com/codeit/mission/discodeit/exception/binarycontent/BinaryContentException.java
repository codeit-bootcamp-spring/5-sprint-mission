package com.codeit.mission.discodeit.exception.binarycontent;

import com.codeit.mission.discodeit.exception.DiscodeitException;
import com.codeit.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public class BinaryContentException extends DiscodeitException {

    public BinaryContentException(ErrorCode errorCode) {
        super(errorCode);
    }

    public BinaryContentException(ErrorCode errorCode, String customMessage) {
        super(errorCode, customMessage);
    }

    public BinaryContentException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }

    public BinaryContentException(ErrorCode errorCode, String customMessage, Throwable cause) {
        super(errorCode, customMessage, cause);
    }

    public BinaryContentException(ErrorCode errorCode, Map<String, Object> details) {
        super(errorCode, details);
    }

    public BinaryContentException(ErrorCode errorCode, String customMessage,
            Map<String, Object> details) {
        super(errorCode, customMessage, details);
    }
}
