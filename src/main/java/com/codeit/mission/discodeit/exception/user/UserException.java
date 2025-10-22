package com.codeit.mission.discodeit.exception.user;

import com.codeit.mission.discodeit.exception.DiscodeitException;
import com.codeit.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public class UserException extends DiscodeitException {

    public UserException(ErrorCode errorCode) {
        super(errorCode);
    }

    public UserException(ErrorCode errorCode, String customMessage) {
        super(errorCode, customMessage);
    }

    public UserException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }

    public UserException(ErrorCode errorCode, String customMessage, Throwable cause) {
        super(errorCode, customMessage, cause);
    }

    public UserException(ErrorCode errorCode, Map<String, Object> details) {
        super(errorCode, details);
    }

    public UserException(ErrorCode errorCode, String customMessage, Map<String, Object> details) {
        super(errorCode, customMessage, details);
    }
}
