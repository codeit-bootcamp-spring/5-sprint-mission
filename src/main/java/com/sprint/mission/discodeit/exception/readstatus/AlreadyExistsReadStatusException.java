package com.sprint.mission.discodeit.exception.readstatus;

public class AlreadyExistsReadStatusException extends RuntimeException {
	public AlreadyExistsReadStatusException() {
		super("이미 존재하는 읽음 상태입니다.");
	}
}
