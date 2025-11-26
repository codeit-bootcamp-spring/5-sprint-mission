package com.sprint.mission.discodeit.exception.message;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class MessageAccessDeniedException extends MessageException {

    public MessageAccessDeniedException() {
        super(ErrorCode.MESSAGE_ACCESS_DENIED);
    }
}
