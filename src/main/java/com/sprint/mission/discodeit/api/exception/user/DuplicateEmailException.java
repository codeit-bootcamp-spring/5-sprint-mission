package com.sprint.mission.discodeit.api.exception.user;

import com.sprint.mission.discodeit.api.exception.ErrorCode;

import java.util.Map;

public class DuplicateEmailException extends UserException {

    public DuplicateEmailException(String email) {
        super(ErrorCode.DUPLICATE_EMAIL, Map.of("email", email));
    }
}
