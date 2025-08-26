package com.sprint.mission.discodeit.exception.message;

public class MessageNotFoundException extends RuntimeException {
	public MessageNotFoundException() {
		super("찾을 수 없는 메시지");
	}
}