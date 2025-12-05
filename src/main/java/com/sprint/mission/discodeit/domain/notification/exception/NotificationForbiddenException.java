package com.sprint.mission.discodeit.domain.notification.exception;

import com.sprint.mission.discodeit.common.exception.ErrorCode;

import java.util.Map;
import java.util.UUID;

public class NotificationForbiddenException extends NotificationException {

    public NotificationForbiddenException(UUID notificationId, UUID userId) {
        super(ErrorCode.NOTIFICATION_FORBIDDEN, Map.of("notificationId", notificationId, "userId", userId));
    }
}
