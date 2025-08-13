package com.sprint.mission.discodeit.exception.channel;

public class PrivateChannelUpdateException extends RuntimeException {
	public PrivateChannelUpdateException() {
		super("private 채널 변경 불가");
	}
}