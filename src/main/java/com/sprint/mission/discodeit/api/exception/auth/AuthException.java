package com.sprint.mission.discodeit.api.exception.auth;

import com.sprint.mission.discodeit.api.exception.DiscodeitException;
import com.sprint.mission.discodeit.api.exception.ErrorCode;

import java.util.Map;

public class AuthException extends DiscodeitException {

    public AuthException(ErrorCode errorCode) {
        super(errorCode);
    }

    public AuthException(ErrorCode errorCode, Map<String, Object> details) {
        super(errorCode, details);
    }
}
