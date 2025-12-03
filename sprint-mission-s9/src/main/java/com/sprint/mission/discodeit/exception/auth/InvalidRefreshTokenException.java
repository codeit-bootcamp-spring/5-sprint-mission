package com.sprint.mission.discodeit.exception.auth;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;

public class InvalidRefreshTokenException extends DiscodeitException {
    public InvalidRefreshTokenException(ErrorCode errorCode) {
        super(errorCode);
    }
}