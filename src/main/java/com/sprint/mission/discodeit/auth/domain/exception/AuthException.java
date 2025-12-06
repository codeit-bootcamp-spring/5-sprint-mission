package com.sprint.mission.discodeit.auth.domain.exception;

import com.sprint.mission.discodeit.common.domain.exception.DiscodeitException;
import com.sprint.mission.discodeit.common.domain.exception.ErrorCode;

import java.util.Map;

public class AuthException extends DiscodeitException {

    public AuthException(ErrorCode errorCode) {
        super(errorCode);
    }

    public AuthException(ErrorCode errorCode, Map<String, Object> details) {
        super(errorCode, details);
    }
}
