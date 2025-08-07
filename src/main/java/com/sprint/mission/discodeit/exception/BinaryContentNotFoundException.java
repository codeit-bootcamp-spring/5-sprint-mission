package com.sprint.mission.discodeit.exception;

public class BinaryContentNotFoundException extends RuntimeException {
	public BinaryContentNotFoundException() {
		super("파일을 찾을 수 없습니다.");
	}
}
