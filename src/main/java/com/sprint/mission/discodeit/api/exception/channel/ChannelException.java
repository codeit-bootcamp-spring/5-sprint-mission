package com.sprint.mission.discodeit.api.exception.channel;

import com.sprint.mission.discodeit.api.exception.DiscodeitException;
import com.sprint.mission.discodeit.api.exception.ErrorCode;

import java.util.Map;

public class ChannelException extends DiscodeitException {

    public ChannelException(ErrorCode errorCode) {
        super(errorCode);
    }

    public ChannelException(ErrorCode errorCode, Map<String, Object> details) {
        super(errorCode, details);
    }
}
