package com.sprint.mission.discodeit.global.exception.auth;

import com.sprint.mission.discodeit.global.exception.ErrorCode;

public class InvalidTokenException extends AuthException {

    public InvalidTokenException() {
        super(ErrorCode.INVALID_TOKEN);
    }
}
