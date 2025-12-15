package com.sprint.mission.discodeit.user.domain.exception;

import com.sprint.mission.discodeit.common.domain.exception.DiscodeitException;
import com.sprint.mission.discodeit.common.domain.exception.ErrorCode;

import java.util.Map;

public class UserException extends DiscodeitException {

    public UserException(ErrorCode errorCode, Map<String, Object> details) {
        super(errorCode, details);
    }

    public UserException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
