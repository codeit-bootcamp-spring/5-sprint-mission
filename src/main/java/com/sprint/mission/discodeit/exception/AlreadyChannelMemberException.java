package com.sprint.mission.discodeit.exception;

public class AlreadyChannelMemberException extends RuntimeException {
	public  AlreadyChannelMemberException() {
		super("이미 참여 중인 채널입니다.");
	}
}
