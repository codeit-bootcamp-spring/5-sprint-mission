package com.sprint.mission.discodeit.api.exception.readstatus;

import com.sprint.mission.discodeit.api.exception.ErrorCode;

import java.util.Map;
import java.util.UUID;

public class ReadStatusForbiddenException extends ReadStatusException {

    public ReadStatusForbiddenException(UUID readStatusId, UUID requesterId) {
        super(ErrorCode.READ_STATUS_FORBIDDEN, Map.of("readStatusId", readStatusId, "requesterId", requesterId));
    }
}
