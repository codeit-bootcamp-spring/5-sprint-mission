package com.sprint.mission.discodeit.domain.auth.exception;

import com.sprint.mission.discodeit.common.exception.ErrorCode;

public class InsufficientRoleException extends AuthException {

    public InsufficientRoleException() {
        super(ErrorCode.INSUFFICIENT_ROLE);
    }
}
