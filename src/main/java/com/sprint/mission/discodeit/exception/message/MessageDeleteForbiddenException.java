package com.sprint.mission.discodeit.exception.message;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class MessageDeleteForbiddenException extends MessageException {

    public MessageDeleteForbiddenException() {
        super(ErrorCode.MESSAGE_FORBIDDEN_DELETE);
    }
}
