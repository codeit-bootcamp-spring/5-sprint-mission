package com.sprint.mission.discodeit.domain.channel.exception;

import com.sprint.mission.discodeit.common.exception.DiscodeitException;
import com.sprint.mission.discodeit.common.exception.ErrorCode;

import java.util.Map;

public class ChannelException extends DiscodeitException {

    public ChannelException(ErrorCode errorCode) {
        super(errorCode);
    }

    public ChannelException(ErrorCode errorCode, Map<String, Object> details) {
        super(errorCode, details);
    }
}
