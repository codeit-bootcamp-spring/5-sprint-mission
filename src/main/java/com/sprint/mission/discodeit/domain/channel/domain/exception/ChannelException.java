package com.sprint.mission.discodeit.domain.channel.domain.exception;

import com.sprint.mission.discodeit.global.error.ErrorCode;
import com.sprint.mission.discodeit.global.exception.DiscodeitException;

import java.util.Map;

public class ChannelException extends DiscodeitException {

    public ChannelException(ErrorCode errorCode) {
        super(errorCode);
    }

    public ChannelException(ErrorCode errorCode, Map<String, Object> details) {
        super(errorCode, details);
    }
}
