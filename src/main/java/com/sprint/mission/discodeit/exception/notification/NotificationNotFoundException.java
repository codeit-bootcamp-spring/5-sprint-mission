package com.sprint.mission.discodeit.exception.notification;

import com.sprint.mission.discodeit.exception.base.DiscodeitException;
import com.sprint.mission.discodeit.exception.base.ErrorCode;

import java.util.Map;

public class NotificationNotFoundException extends DiscodeitException {

    public NotificationNotFoundException() {
        super(ErrorCode.NOTIFICATION_NOT_FOUND);
    }

    public NotificationNotFoundException(Map<String, Object> details) {
        super(ErrorCode.NOTIFICATION_NOT_FOUND, details);
    }
}
