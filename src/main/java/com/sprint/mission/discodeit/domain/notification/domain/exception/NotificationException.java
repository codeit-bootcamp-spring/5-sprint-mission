package com.sprint.mission.discodeit.domain.notification.domain.exception;

import com.sprint.mission.discodeit.domain.common.exception.DiscodeitException;
import com.sprint.mission.discodeit.domain.common.exception.ErrorCode;

import java.util.Map;

public class NotificationException extends DiscodeitException {

    public NotificationException(ErrorCode errorCode) {
        super(errorCode);
    }

    public NotificationException(ErrorCode errorCode, Map<String, Object> details) {
        super(errorCode, details);
    }
}
