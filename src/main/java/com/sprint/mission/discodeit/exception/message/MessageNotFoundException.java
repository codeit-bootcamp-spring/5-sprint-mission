package com.sprint.mission.discodeit.exception.message;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;
import java.util.UUID;

public class MessageNotFoundException extends MessageException {

    public MessageNotFoundException() {
        super(ErrorCode.MESSAGE_NOT_FOUND);
    }

    public MessageNotFoundException(UUID messageId) {
        super(ErrorCode.MESSAGE_NOT_FOUND, Map.of("messageId", messageId));
    }
}
