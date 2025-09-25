package com.sprint.mission.discodeit.exception.message;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class MessageNotFoundException extends MessageException {
	public MessageNotFoundException() {
		super(ErrorCode.MESSAGE_NOT_FOUND);
	}


	public static MessageNotFoundException withId(String messageId) {
		MessageNotFoundException exception = new MessageNotFoundException();
		exception.addDetails("message id", messageId);
		return exception;
	}

}

