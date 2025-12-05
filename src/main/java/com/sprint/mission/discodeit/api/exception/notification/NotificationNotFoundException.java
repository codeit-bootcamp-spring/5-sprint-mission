package com.sprint.mission.discodeit.api.exception.notification;

import com.sprint.mission.discodeit.api.exception.ErrorCode;

import java.util.Map;
import java.util.UUID;

public class NotificationNotFoundException extends NotificationException {

    public NotificationNotFoundException(UUID notificationId) {
        super(ErrorCode.NOTIFICATION_NOT_FOUND, Map.of("notificationId", notificationId));
    }
}
