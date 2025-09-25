package com.sprint.mission.discodeit.domain.message.exception;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;

public class MessageNotFoundException extends MessageException {
    public MessageNotFoundException(Long messageId) {
        super(ErrorCode.MESSAGE_NOT_FOUND, Map.of("messageId", messageId));
    }
}
