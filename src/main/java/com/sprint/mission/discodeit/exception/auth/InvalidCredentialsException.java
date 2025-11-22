package com.sprint.mission.discodeit.exception.auth;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class InvalidCredentialsException extends AuthException {

    public InvalidCredentialsException() {
        super(ErrorCode.WRONG_USERNAME_OR_PASSWORD);
    }
}
