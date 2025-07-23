package com.sprint.mission.discodeit.exception;

public class DuplicateChannelNameException extends RuntimeException {
	public DuplicateChannelNameException(String channelName) {
		super("이미 존재하는 채널명" + channelName);
	}
}
