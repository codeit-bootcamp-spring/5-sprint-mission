package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class UserNotFoundException extends UserException {

    public UserNotFoundException() {
        super(ErrorCode.BINARY_CONTENT_NOT_FOUND);
    }
}
