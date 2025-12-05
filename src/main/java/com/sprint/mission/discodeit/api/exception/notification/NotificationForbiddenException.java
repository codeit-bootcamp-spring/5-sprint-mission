package com.sprint.mission.discodeit.api.exception.notification;

import com.sprint.mission.discodeit.api.exception.ErrorCode;

import java.util.Map;
import java.util.UUID;

public class NotificationForbiddenException extends NotificationException {

    public NotificationForbiddenException(UUID notificationId, UUID userId) {
        super(ErrorCode.NOTIFICATION_FORBIDDEN, Map.of("notificationId", notificationId, "userId", userId));
    }
}
