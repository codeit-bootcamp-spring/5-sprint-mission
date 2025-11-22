package com.sprint.mission.discodeit.exception.auth;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class UserInvalidCredentialsException extends AuthException {

    public UserInvalidCredentialsException() {
        super(ErrorCode.USER_INVALID_CREDENTIALS);
    }
}
