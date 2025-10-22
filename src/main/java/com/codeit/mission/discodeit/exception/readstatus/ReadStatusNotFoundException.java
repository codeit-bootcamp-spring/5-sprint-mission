package com.codeit.mission.discodeit.exception.readstatus;

import com.codeit.mission.discodeit.exception.ErrorCode;
import java.util.Map;
import java.util.UUID;

public class ReadStatusNotFoundException extends ReadStatusException {

    public ReadStatusNotFoundException(UUID readStatusId) {
        super(ErrorCode.READ_STATUS_NOT_FOUND, "ReadStatus with id " + readStatusId + " not found",
                Map.of("readStatusId", readStatusId));
    }

    public ReadStatusNotFoundException(UUID userId, UUID channelId) {
        super(ErrorCode.READ_STATUS_NOT_FOUND,
                "ReadStatus with userId " + userId + " and channelId " + channelId + " not found",
                Map.of("userId", userId, "channelId", channelId));
    }
}
