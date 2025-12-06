package com.sprint.mission.discodeit.domain.message.domain.exception;

import com.sprint.mission.discodeit.global.error.ErrorCode;
import com.sprint.mission.discodeit.global.exception.DiscodeitException;

import java.util.Map;

public class MessageException extends DiscodeitException {

    public MessageException(ErrorCode errorCode) {
        super(errorCode);
    }

    public MessageException(ErrorCode errorCode, Map<String, Object> details) {
        super(errorCode, details);
    }
}
