package com.sprint.mission.discodeit.global.exception.readstatus;

import com.sprint.mission.discodeit.global.exception.ErrorCode;

import java.util.Map;
import java.util.UUID;

public class ReadStatusForbiddenException extends ReadStatusException {

    public ReadStatusForbiddenException() {
        super(ErrorCode.READ_STATUS_FORBIDDEN);
    }

    public ReadStatusForbiddenException(UUID readStatusId, UUID requesterId) {
        super(ErrorCode.READ_STATUS_FORBIDDEN, Map.of("readStatusId", readStatusId, "requesterId", requesterId));
    }
}
