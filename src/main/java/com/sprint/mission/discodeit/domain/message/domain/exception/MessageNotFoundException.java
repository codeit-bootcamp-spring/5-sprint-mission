package com.sprint.mission.discodeit.domain.message.domain.exception;

import com.sprint.mission.discodeit.global.error.ErrorCode;

import java.util.Map;
import java.util.UUID;

public class MessageNotFoundException extends MessageException {

    public MessageNotFoundException(UUID messageId) {
        super(ErrorCode.MESSAGE_NOT_FOUND, Map.of("messageId", messageId));
    }
}
