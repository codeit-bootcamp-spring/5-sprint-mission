package com.codeit.mission.discodeit.exception.readstatus;

import com.codeit.mission.discodeit.exception.ErrorCode;
import java.util.Map;
import java.util.UUID;

public class ReadStatusAlreadyExistsException extends ReadStatusException {

    public ReadStatusAlreadyExistsException(UUID userId, UUID channelId) {
        super(ErrorCode.READ_STATUS_ALREADY_EXISTS,
                "ReadStatus with userId " + userId + " and channelId " + channelId
                        + " already exists", Map.of("userId", userId, "channelId", channelId));
    }
}
