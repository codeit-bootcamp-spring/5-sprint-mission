package com.sprint.mission.discodeit.domain.readstatus.exception;

import com.sprint.mission.discodeit.domain.common.exception.ErrorCode;

import java.util.Map;
import java.util.UUID;

public class ReadStatusForbiddenException extends ReadStatusException {

    public ReadStatusForbiddenException(UUID readStatusId, UUID requesterId) {
        super(ErrorCode.READ_STATUS_FORBIDDEN, Map.of("readStatusId", readStatusId, "requesterId", requesterId));
    }
}
