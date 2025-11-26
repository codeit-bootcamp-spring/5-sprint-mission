package com.sprint.mission.discodeit.exception.message;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class MessageEditForbiddenException extends MessageException {

    public MessageEditForbiddenException() {
        super(ErrorCode.MESSAGE_FORBIDDEN_EDIT);
    }
}
