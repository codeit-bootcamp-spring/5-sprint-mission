package com.sprint.mission.discodeit.exception;

public class DuplicateLoginIdException extends RuntimeException {
	public DuplicateLoginIdException() {
		super("이미 존재하는 아이디입니다. ");
	}
}
