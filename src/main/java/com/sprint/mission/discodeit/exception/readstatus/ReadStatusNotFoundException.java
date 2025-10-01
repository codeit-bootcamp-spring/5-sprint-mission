package com.sprint.mission.discodeit.exception.readstatus;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.UUID;

public class ReadStatusNotFoundException extends ReadStatusException {
    public ReadStatusNotFoundException() {
        super(ErrorCode.READ_STATUS_NOT_FOUND);
    }

    public static ReadStatusNotFoundException withReadStatusId(UUID readStatusId) {
        ReadStatusNotFoundException exception = new ReadStatusNotFoundException();
        exception.addDetail("readStatusId", readStatusId);
        return exception;
    }

    public static ReadStatusNotFoundException withUserAndChannel(UUID userId, UUID channelId) {
        ReadStatusNotFoundException exception = new ReadStatusNotFoundException();
        exception.addDetail("userId", userId);
        exception.addDetail("channelId", channelId);
        return exception;
    }

    public static ReadStatusNotFoundException withUserId(UUID userId) {
        ReadStatusNotFoundException exception = new ReadStatusNotFoundException();
        exception.addDetail("userId", userId);
        return exception;
    }

    public static ReadStatusNotFoundException withChannelId(UUID channelId) {
        ReadStatusNotFoundException exception = new ReadStatusNotFoundException();
        exception.addDetail("channelId", channelId);
        return exception;
    }
}
