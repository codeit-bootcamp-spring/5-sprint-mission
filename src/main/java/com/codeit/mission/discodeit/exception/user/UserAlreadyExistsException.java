package com.codeit.mission.discodeit.exception.user;

import com.codeit.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public class UserAlreadyExistsException extends UserException {

    public UserAlreadyExistsException(String email) {
        super(ErrorCode.USER_ALREADY_EXISTS,
                "User with email " + email + " already exists",
                Map.of("email", email));
    }

    public UserAlreadyExistsException(String field, String value) {
        super(ErrorCode.USER_ALREADY_EXISTS,
                "User with " + field + " " + value + " already exists",
                Map.of(field, value));
    }
}
