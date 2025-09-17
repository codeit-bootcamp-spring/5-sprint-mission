package com.sprint.mission.discodeit.domain.channel.exception;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;

public class ChannelException extends DiscodeitException {
    protected ChannelException(ErrorCode errorCode) {
        super(errorCode);
    }

    protected ChannelException(ErrorCode errorCode, Map<String, Object> details) {
        super(errorCode, details);
    }
}
