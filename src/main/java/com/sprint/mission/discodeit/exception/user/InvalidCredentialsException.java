package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;

public class InvalidCredentialsException extends UserException {
    public InvalidCredentialsException() {
        super(ErrorCode.INVALID_PASSWORD);
    }

    public static InvalidCredentialsException wrongPassword() {
        return new InvalidCredentialsException();
    }

    public static InvalidCredentialsException wrongUsername(String username) {
        InvalidCredentialsException exception = new InvalidCredentialsException();
        exception.addDetail("username", username);
        return exception;
    }

}
