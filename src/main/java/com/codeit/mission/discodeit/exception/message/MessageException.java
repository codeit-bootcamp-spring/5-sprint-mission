package com.codeit.mission.discodeit.exception.message;

import com.codeit.mission.discodeit.exception.DiscodeitException;
import com.codeit.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public class MessageException extends DiscodeitException {

    public MessageException(ErrorCode errorCode) {
        super(errorCode);
    }

    public MessageException(ErrorCode errorCode, String customMessage) {
        super(errorCode, customMessage);
    }

    public MessageException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }

    public MessageException(ErrorCode errorCode, String customMessage, Throwable cause) {
        super(errorCode, customMessage, cause);
    }

    public MessageException(ErrorCode errorCode, Map<String, Object> details) {
        super(errorCode, details);
    }

    public MessageException(ErrorCode errorCode, String customMessage,
            Map<String, Object> details) {
        super(errorCode, customMessage, details);
    }
}
