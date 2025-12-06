package com.sprint.mission.discodeit.domain.auth.exception;

import com.sprint.mission.discodeit.global.error.ErrorCode;

import java.util.Map;

public class InvalidCredentialsException extends AuthException {

    public InvalidCredentialsException(String username) {
        super(ErrorCode.INVALID_CREDENTIALS, Map.of("username", username));
    }
}
