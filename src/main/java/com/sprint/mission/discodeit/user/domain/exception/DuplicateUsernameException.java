package com.sprint.mission.discodeit.user.domain.exception;

import com.sprint.mission.discodeit.common.domain.exception.ErrorCode;

import java.util.Map;

public class DuplicateUsernameException extends UserException {

    public DuplicateUsernameException(String username) {
        super(ErrorCode.DUPLICATE_USERNAME, Map.of("username", username));
    }
}
