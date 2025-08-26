package com.sprint.mission.discodeit.exception.userstatus;

public class AlreadyExistsUserStatusException extends RuntimeException {
	public AlreadyExistsUserStatusException() {
		super("이미 존재하는 사용자 상태입니다.");
	}
}
