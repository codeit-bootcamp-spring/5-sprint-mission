package com.sprint.mission.discodeit.exception.message;

public class UnauthorizedMessageAccessException extends RuntimeException {
	public UnauthorizedMessageAccessException() {
		super("메시지 수정 권한이 없습니다");
	}
}
