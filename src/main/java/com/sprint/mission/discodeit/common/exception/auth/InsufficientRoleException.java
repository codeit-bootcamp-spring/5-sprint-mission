package com.sprint.mission.discodeit.common.exception.auth;

import com.sprint.mission.discodeit.common.exception.ErrorCode;

import java.util.Map;

public class InsufficientRoleException extends AuthException {

    public InsufficientRoleException() {
        super(ErrorCode.INSUFFICIENT_ROLE);
    }

    public InsufficientRoleException(String requiredRole) {
        super(ErrorCode.INSUFFICIENT_ROLE, Map.of("requiredRole", requiredRole));
    }

    public InsufficientRoleException(String requiredRole, Throwable cause) {
        super(ErrorCode.INSUFFICIENT_ROLE, Map.of("requiredRole", requiredRole), cause);
    }
}
