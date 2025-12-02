package com.sprint.mission.discodeit.exception.notification;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.UUID;

public class UnauthorizedNotificationAccessException extends DiscodeitException {

    public UnauthorizedNotificationAccessException() {
        super(ErrorCode.UNAUTHORIZED_NOTIFICATION_ACCESS);
    }

    public static UnauthorizedNotificationAccessException withDetails(UUID notificationId, UUID userId) {
        UnauthorizedNotificationAccessException exception = new UnauthorizedNotificationAccessException();
        exception.addDetail("notificationId", notificationId);
        exception.addDetail("userId", userId);
        return exception;
    }
}