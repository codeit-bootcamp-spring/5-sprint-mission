package com.sprint.mission.discodeit.message.domain.exception;

import com.sprint.mission.discodeit.common.domain.exception.ErrorCode;

public class EmptyMessageContentException extends MessageException {

    public EmptyMessageContentException() {
        super(ErrorCode.MESSAGE_EMPTY_CONTENT);
    }
}
