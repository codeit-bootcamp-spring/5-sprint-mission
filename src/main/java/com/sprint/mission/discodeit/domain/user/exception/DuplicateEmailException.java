package com.sprint.mission.discodeit.domain.user.exception;

import com.sprint.mission.discodeit.common.exception.ErrorCode;

import java.util.Map;

public class DuplicateEmailException extends UserException {

    public DuplicateEmailException(String email) {
        super(ErrorCode.DUPLICATE_EMAIL, Map.of("email", email));
    }
}
