package com.sprint.mission.discodeit.domain.auth.domain.exception;

import com.sprint.mission.discodeit.domain.common.exception.ErrorCode;

public class InsufficientRoleException extends AuthException {

    public InsufficientRoleException() {
        super(ErrorCode.INSUFFICIENT_ROLE);
    }
}
