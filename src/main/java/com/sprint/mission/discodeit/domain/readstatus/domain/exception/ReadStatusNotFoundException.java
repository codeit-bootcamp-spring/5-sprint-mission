package com.sprint.mission.discodeit.domain.readstatus.domain.exception;

import com.sprint.mission.discodeit.global.error.ErrorCode;

import java.util.Map;
import java.util.UUID;

public class ReadStatusNotFoundException extends ReadStatusException {

    public ReadStatusNotFoundException(UUID readStatusId) {
        super(ErrorCode.READ_STATUS_NOT_FOUND, Map.of("readStatusId", readStatusId));
    }
}
