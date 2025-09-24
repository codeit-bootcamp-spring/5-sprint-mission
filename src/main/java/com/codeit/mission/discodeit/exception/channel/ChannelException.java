package com.codeit.mission.discodeit.exception.channel;

import com.codeit.mission.discodeit.exception.DiscodeitException;
import com.codeit.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public class ChannelException extends DiscodeitException {

    public ChannelException(ErrorCode errorCode) {
        super(errorCode);
    }

    public ChannelException(ErrorCode errorCode, String customMessage) {
        super(errorCode, customMessage);
    }

    public ChannelException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }

    public ChannelException(ErrorCode errorCode, String customMessage, Throwable cause) {
        super(errorCode, customMessage, cause);
    }

    public ChannelException(ErrorCode errorCode, Map<String, Object> details) {
        super(errorCode, details);
    }

    public ChannelException(ErrorCode errorCode, String customMessage,
            Map<String, Object> details) {
        super(errorCode, customMessage, details);
    }
}