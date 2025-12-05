package com.sprint.mission.discodeit.api.exception.user;

import com.sprint.mission.discodeit.api.exception.ErrorCode;

import java.util.Map;
import java.util.UUID;

public class UserNotFoundException extends UserException {

    public UserNotFoundException(UUID userId) {
        super(ErrorCode.USER_NOT_FOUND, Map.of("userId", userId));
    }
}
