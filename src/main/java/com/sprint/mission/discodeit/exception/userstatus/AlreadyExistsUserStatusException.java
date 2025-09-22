package com.sprint.mission.discodeit.exception.userstatus;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.UUID;

public class AlreadyExistsUserStatusException extends UserStatusException {
    public AlreadyExistsUserStatusException() {
        super(ErrorCode.ALREADY_EXISTS_USER_STATUS);
    }

    public static AlreadyExistsUserStatusException withUserId(UUID userId) {
        AlreadyExistsUserStatusException exception = new AlreadyExistsUserStatusException();
        exception.addDetail("userId", userId);
        return exception;
    }
}
