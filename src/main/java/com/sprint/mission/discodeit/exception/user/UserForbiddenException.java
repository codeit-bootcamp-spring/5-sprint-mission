package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;
import java.util.UUID;

public class UserForbiddenException extends UserException {

    public UserForbiddenException() {
        super(ErrorCode.USER_FORBIDDEN);
    }

    public UserForbiddenException(UUID targetUserId, UUID requesterId) {
        super(ErrorCode.USER_FORBIDDEN, Map.of("targetUserId", targetUserId, "requesterId", requesterId));
    }
}
