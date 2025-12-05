package com.sprint.mission.discodeit.api.exception.notification;

import com.sprint.mission.discodeit.api.exception.DiscodeitException;
import com.sprint.mission.discodeit.api.exception.ErrorCode;

import java.util.Map;

public class NotificationException extends DiscodeitException {

    public NotificationException(ErrorCode errorCode) {
        super(errorCode);
    }

    public NotificationException(ErrorCode errorCode, Map<String, Object> details) {
        super(errorCode, details);
    }
}
