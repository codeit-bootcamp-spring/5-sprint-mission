package com.sprint.mission.discodeit.exception.user;

public class UserNotFoundException extends RuntimeException {
	public UserNotFoundException(String message) {
		super(message);
	}

	public UserNotFoundException() {
		super("없는 아이디 입니다.");
	}
}
