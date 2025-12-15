package com.sprint.mission.discodeit.notification.domain.exception;

import com.sprint.mission.discodeit.common.domain.exception.ErrorCode;

import java.util.Map;
import java.util.UUID;

public class NotificationCheckForbiddenException extends NotificationException {

    public NotificationCheckForbiddenException(UUID notificationId, UUID userId) {
        super(ErrorCode.NOTIFICATION_CHECK_FORBIDDEN, Map.of("notificationId", notificationId, "userId", userId));
    }
}
