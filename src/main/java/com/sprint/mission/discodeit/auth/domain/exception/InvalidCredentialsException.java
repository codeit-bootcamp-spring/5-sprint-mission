package com.sprint.mission.discodeit.auth.domain.exception;

import com.sprint.mission.discodeit.common.domain.exception.ErrorCode;

import java.util.Map;

public class InvalidCredentialsException extends AuthException {

    public InvalidCredentialsException(String username) {
        super(ErrorCode.INVALID_CREDENTIALS, Map.of("username", username));
    }
}
