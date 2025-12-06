package com.sprint.mission.discodeit.domain.auth.domain.exception;

import com.sprint.mission.discodeit.global.error.ErrorCode;

import java.util.Map;

public class InvalidTokenException extends AuthException {

    public InvalidTokenException() {
        super(ErrorCode.INVALID_TOKEN);
    }

    public InvalidTokenException(String username) {
        super(ErrorCode.INVALID_TOKEN, Map.of("username", username));
    }
}
