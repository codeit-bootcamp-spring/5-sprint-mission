package com.sprint.mission.discodeit.exception.readstatus;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.UUID;

public class AlreadyExistsReadStatusException extends ReadStatusException {
    public AlreadyExistsReadStatusException() {
        super(ErrorCode.ALREADY_EXISTS_READ_STATUS);
    }

    public static AlreadyExistsReadStatusException withUserAndChannel(UUID userId, UUID channelId) {
        AlreadyExistsReadStatusException exception = new AlreadyExistsReadStatusException();
        exception.addDetail("userId", userId);
        exception.addDetail("channelId", channelId);
        return exception;
    }
}
