package com.sprint.mission.discodeit.exception;

public class MessageNotFoundException extends RuntimeException {
	public MessageNotFoundException(String message) {
		super(message);
	}

	public MessageNotFoundException(java.util.UUID messageId) {
		super("찾을 수 없는 메시지, ID: " + messageId);
	}
}