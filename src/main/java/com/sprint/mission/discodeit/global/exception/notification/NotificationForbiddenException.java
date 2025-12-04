package com.sprint.mission.discodeit.global.exception.notification;

import com.sprint.mission.discodeit.global.exception.ErrorCode;

import java.util.Map;
import java.util.UUID;

public class NotificationForbiddenException extends NotificationException {

    public NotificationForbiddenException() {
        super(ErrorCode.NOTIFICATION_FORBIDDEN);
    }

    public NotificationForbiddenException(UUID notificationId, UUID userId) {
        super(ErrorCode.NOTIFICATION_FORBIDDEN, Map.of("notificationId", notificationId, "userId", userId));
    }
}
