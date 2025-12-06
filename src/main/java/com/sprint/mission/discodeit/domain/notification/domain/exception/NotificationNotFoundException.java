package com.sprint.mission.discodeit.domain.notification.domain.exception;

import com.sprint.mission.discodeit.global.error.ErrorCode;

import java.util.Map;
import java.util.UUID;

public class NotificationNotFoundException extends NotificationException {

    public NotificationNotFoundException(UUID notificationId) {
        super(ErrorCode.NOTIFICATION_NOT_FOUND, Map.of("notificationId", notificationId));
    }
}
