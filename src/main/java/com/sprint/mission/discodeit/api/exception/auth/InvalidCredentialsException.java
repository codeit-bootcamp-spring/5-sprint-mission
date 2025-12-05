package com.sprint.mission.discodeit.api.exception.auth;

import com.sprint.mission.discodeit.api.exception.ErrorCode;

import java.util.Map;

public class InvalidCredentialsException extends AuthException {

    public InvalidCredentialsException(String username) {
        super(ErrorCode.INVALID_CREDENTIALS, Map.of("username", username));
    }
}
