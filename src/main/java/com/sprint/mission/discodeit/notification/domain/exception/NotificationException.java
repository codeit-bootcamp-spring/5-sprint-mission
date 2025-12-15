package com.sprint.mission.discodeit.notification.domain.exception;

import com.sprint.mission.discodeit.common.domain.exception.DiscodeitException;
import com.sprint.mission.discodeit.common.domain.exception.ErrorCode;

import java.util.Map;

public class NotificationException extends DiscodeitException {

    public NotificationException(ErrorCode errorCode, Map<String, Object> details) {
        super(errorCode, details);
    }
}
