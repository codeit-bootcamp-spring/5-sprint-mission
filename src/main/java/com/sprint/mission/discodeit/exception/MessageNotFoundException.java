package com.sprint.mission.discodeit.exception;

import java.util.UUID;

public class MessageNotFoundException extends RuntimeException {
	public MessageNotFoundException(String message) {
		super(message);
	}

	public MessageNotFoundException(UUID messageId) {
		super("찾을 수 없는 메시지, ID: " + messageId);
	}

	public MessageNotFoundException() {
		super("찾을 수 없는 메시지");
	}
}