package com.sprint.mission.discodeit.domain.user.domain.exception;

import com.sprint.mission.discodeit.global.error.ErrorCode;

import java.util.Map;

public class DuplicateUsernameException extends UserException {

    public DuplicateUsernameException(String username) {
        super(ErrorCode.DUPLICATE_USERNAME, Map.of("username", username));
    }
}
