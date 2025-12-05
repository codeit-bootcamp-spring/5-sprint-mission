package com.sprint.mission.discodeit.domain.user.exception;

import com.sprint.mission.discodeit.domain.common.exception.DiscodeitException;
import com.sprint.mission.discodeit.domain.common.exception.ErrorCode;

import java.util.Map;

public class UserException extends DiscodeitException {

    public UserException(ErrorCode errorCode) {
        super(errorCode);
    }

    public UserException(ErrorCode errorCode, Map<String, Object> details) {
        super(errorCode, details);
    }

    public UserException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
