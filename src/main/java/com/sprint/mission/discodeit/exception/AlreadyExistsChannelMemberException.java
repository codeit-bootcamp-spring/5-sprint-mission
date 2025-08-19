package com.sprint.mission.discodeit.exception;

public class AlreadyExistsChannelMemberException extends RuntimeException {
	public AlreadyExistsChannelMemberException() {
		super("이미 참여 중인 채널입니다.");
	}
}
