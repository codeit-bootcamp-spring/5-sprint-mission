package com.sprint.mission.discodeit.domain.message.exception;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;

public class MessageException extends DiscodeitException {
    protected MessageException(ErrorCode errorCode) {
        super(errorCode);
    }
    protected MessageException(ErrorCode errorCode, Map<String, Object> details) {
        super(errorCode, details);
    }
}
