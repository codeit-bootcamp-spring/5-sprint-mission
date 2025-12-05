package com.sprint.mission.discodeit.api.exception.user;

import com.sprint.mission.discodeit.api.exception.ErrorCode;

import java.util.Map;

public class DuplicateUsernameException extends UserException {

    public DuplicateUsernameException(String username) {
        super(ErrorCode.DUPLICATE_USERNAME, Map.of("username", username));
    }
}
