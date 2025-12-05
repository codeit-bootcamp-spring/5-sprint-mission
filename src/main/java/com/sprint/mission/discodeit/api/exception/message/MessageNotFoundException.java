package com.sprint.mission.discodeit.api.exception.message;

import com.sprint.mission.discodeit.api.exception.ErrorCode;

import java.util.Map;
import java.util.UUID;

public class MessageNotFoundException extends MessageException {

    public MessageNotFoundException(UUID messageId) {
        super(ErrorCode.MESSAGE_NOT_FOUND, Map.of("messageId", messageId));
    }
}
