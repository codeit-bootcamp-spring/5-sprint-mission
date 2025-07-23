package com.sprint.mission.discodeit.exception;

public class InvalidPasswordException extends RuntimeException {
	public InvalidPasswordException() {
		super("틀린 비밀번호");
	}
}
