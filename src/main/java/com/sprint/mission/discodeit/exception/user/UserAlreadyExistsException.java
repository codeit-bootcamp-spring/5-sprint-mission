package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class UserAlreadyExistsException extends UserException {

    public UserAlreadyExistsException() {
        super(ErrorCode.DUPLICATE_USER);
    }
}
