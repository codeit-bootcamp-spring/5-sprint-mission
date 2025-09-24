package com.codeit.mission.discodeit.exception.user;

import com.codeit.mission.discodeit.exception.ErrorCode;
import java.util.Map;
import java.util.UUID;

public class UserNotFoundException extends UserException {

    public UserNotFoundException(UUID userId) {
        super(ErrorCode.USER_NOT_FOUND,
                Map.of("userId", userId));
    }

    public UserNotFoundException(String username) {
        super(ErrorCode.USER_NOT_FOUND,
                "User with username " + username + " not found",
                Map.of("username", username));
    }
}
