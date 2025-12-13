package com.sprint.mission.discodeit.auth.domain.exception;

import com.sprint.mission.discodeit.common.domain.exception.ErrorCode;

public class InvalidTokenException extends AuthException {

    public InvalidTokenException() {
        super(ErrorCode.INVALID_TOKEN);
    }
}
