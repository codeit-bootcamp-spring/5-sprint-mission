package com.sprint.mission.discodeit.exception.channel;

public class NotChannelMemberException extends RuntimeException {
	public NotChannelMemberException() {
		super("채널의 멤버가 아닙니다.");
	}
}
