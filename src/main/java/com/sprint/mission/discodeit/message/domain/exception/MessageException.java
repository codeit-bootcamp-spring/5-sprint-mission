package com.sprint.mission.discodeit.message.domain.exception;

import com.sprint.mission.discodeit.common.domain.exception.DiscodeitException;
import com.sprint.mission.discodeit.common.domain.exception.ErrorCode;

import java.util.Map;

public class MessageException extends DiscodeitException {

    public MessageException(ErrorCode errorCode) {
        super(errorCode);
    }

    public MessageException(ErrorCode errorCode, Map<String, Object> details) {
        super(errorCode, details);
    }
}
