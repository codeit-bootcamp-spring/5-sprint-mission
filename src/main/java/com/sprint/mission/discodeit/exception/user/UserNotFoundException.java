package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class UserNotFoundException extends UserException {
	public UserNotFoundException() {
		super(ErrorCode.USER_NOT_FOUND);
	}

	public static UserNotFoundException withUsername(String username) {
		UserNotFoundException exception = new UserNotFoundException();
		exception.addDetails("username", username);
		return exception;
	}

	public static UserNotFoundException withId(String userId) {
		UserNotFoundException exception = new UserNotFoundException();
		exception.addDetails("userId", userId);
		return exception;
	}

	public static UserNotFoundException withEmail(String email) {
		UserNotFoundException exception = new UserNotFoundException();
		exception.addDetails("email", email);
		return exception;
	}

	public static UserNotFoundException withMessage(String message) {
		UserNotFoundException exception = new UserNotFoundException();
		exception.addDetails("message", message);
		return exception;
	}
}
