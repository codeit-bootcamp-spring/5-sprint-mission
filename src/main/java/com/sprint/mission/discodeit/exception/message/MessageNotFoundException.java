package com.sprint.mission.discodeit.exception.message;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class MessageNotFoundException extends MessageException {

    public MessageNotFoundException() {
        super(ErrorCode.BINARY_CONTENT_NOT_FOUND);
    }
}
