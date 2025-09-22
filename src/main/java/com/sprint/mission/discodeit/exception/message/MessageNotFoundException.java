package com.sprint.mission.discodeit.exception.message;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.UUID;

public class MessageNotFoundException extends MessageException {
    public MessageNotFoundException() {
        super(ErrorCode.MESSAGE_NOT_FOUND);
    }

    public static MessageNotFoundException withMessageId(UUID messageId) {
        MessageNotFoundException exception = new MessageNotFoundException();
        exception.addDetail("messageId", messageId);
        return exception;
    }

    public static MessageNotFoundException withChannelId(UUID channelId) {
        MessageNotFoundException exception = new MessageNotFoundException();
        exception.addDetail("channelId", channelId);
        return exception;
    }
}