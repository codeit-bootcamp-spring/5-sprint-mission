package com.sprint.mission.discodeit.domain.user.exception;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;

public class UserNotFoundException extends UserException {
    public UserNotFoundException(Long userId) {
        super(ErrorCode.USER_NOT_FOUND, Map.of("userId", userId));
    }
}