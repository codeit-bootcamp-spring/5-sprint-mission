package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.UUID;

public class InvalidCredentialsException extends DiscodeitException {
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
