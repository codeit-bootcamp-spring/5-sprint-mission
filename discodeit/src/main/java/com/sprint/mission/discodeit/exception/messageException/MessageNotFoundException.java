package com.sprint.mission.discodeit.exception.messageException;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class MessageNotFoundException extends MessageException {
    public MessageNotFoundException() {super(ErrorCode.MESSAGE_NOT_FOUND);}

    public static MessageNotFoundException withId(String messageId) {
        MessageNotFoundException messageNotFoundException = new MessageNotFoundException();
        messageNotFoundException.addDetail("messageId", messageId);
        return  messageNotFoundException;
    }
}
