package com.sprint.mission.discodeit.domain.auth.domain.exception;

import com.sprint.mission.discodeit.global.error.ErrorCode;
import com.sprint.mission.discodeit.global.exception.DiscodeitException;

import java.util.Map;

public class AuthException extends DiscodeitException {

    public AuthException(ErrorCode errorCode) {
        super(errorCode);
    }

    public AuthException(ErrorCode errorCode, Map<String, Object> details) {
        super(errorCode, details);
    }
}
