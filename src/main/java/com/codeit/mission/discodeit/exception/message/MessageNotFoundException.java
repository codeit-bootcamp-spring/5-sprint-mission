package com.codeit.mission.discodeit.exception.message;

import com.codeit.mission.discodeit.exception.ErrorCode;
import java.util.Map;
import java.util.UUID;

public class MessageNotFoundException extends MessageException {

    public MessageNotFoundException(UUID messageId) {
        super(ErrorCode.MESSAGE_NOT_FOUND, "Message with id " + messageId + " not found",
                Map.of("messageId", messageId));
    }
}
