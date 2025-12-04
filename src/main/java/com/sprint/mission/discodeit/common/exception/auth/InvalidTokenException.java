package com.sprint.mission.discodeit.common.exception.auth;

import com.sprint.mission.discodeit.common.exception.ErrorCode;

public class InvalidTokenException extends AuthException {

    public InvalidTokenException() {
        super(ErrorCode.INVALID_TOKEN);
    }
}
