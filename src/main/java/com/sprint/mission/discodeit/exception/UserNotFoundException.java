package com.sprint.mission.discodeit.exception;

import java.util.UUID;

public class UserNotFoundException extends RuntimeException {
	public UserNotFoundException(String message) {
		super(message);
	}

	public UserNotFoundException(UUID id) {
		super("찾을 수 없는 유저: " + id);
	}

	public UserNotFoundException(String field, String value) {
		super("찾을 수 없는 유저 [" + field + "]: " + value);
	}
}
