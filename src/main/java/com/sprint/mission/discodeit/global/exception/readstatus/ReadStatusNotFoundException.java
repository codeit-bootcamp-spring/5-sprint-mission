package com.sprint.mission.discodeit.global.exception.readstatus;

import com.sprint.mission.discodeit.global.exception.ErrorCode;

import java.util.Map;
import java.util.UUID;

public class ReadStatusNotFoundException extends ReadStatusException {

    public ReadStatusNotFoundException() {
        super(ErrorCode.READ_STATUS_NOT_FOUND);
    }

    public ReadStatusNotFoundException(UUID readStatusId) {
        super(ErrorCode.READ_STATUS_NOT_FOUND, Map.of("readStatusId", readStatusId));
    }

    public ReadStatusNotFoundException(UUID userId, UUID channelId) {
        super(ErrorCode.READ_STATUS_NOT_FOUND, Map.of("userId", userId, "channelId", channelId));
    }
}
