package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class DuplicateUserException extends UserException {

    public DuplicateUserException() {
        super(ErrorCode.DUPLICATE_USER);
    }

    public static DuplicateUserException withEmail(String email) {
        DuplicateUserException exception = new DuplicateUserException();
        exception.addDetail("email", email);
        return exception;
    }

    public static DuplicateUserException withUsername(String username) {
        DuplicateUserException exception = new DuplicateUserException();
        exception.addDetail("username", username);
        return exception;
    }
}
