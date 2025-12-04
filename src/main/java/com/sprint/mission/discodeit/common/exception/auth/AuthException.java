package com.sprint.mission.discodeit.common.exception.auth;

import com.sprint.mission.discodeit.common.exception.DiscodeitException;
import com.sprint.mission.discodeit.common.exception.ErrorCode;

import java.util.Map;

public class AuthException extends DiscodeitException {

    public AuthException(ErrorCode errorCode) {
        super(errorCode);
    }

    public AuthException(ErrorCode errorCode, Map<String, Object> details) {
        super(errorCode, details);
    }

    public AuthException(ErrorCode errorCode, Map<String, Object> details, Throwable cause) {
        super(errorCode, details, cause);
    }
}
