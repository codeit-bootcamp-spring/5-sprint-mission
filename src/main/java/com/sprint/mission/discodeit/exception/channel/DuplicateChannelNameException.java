package com.sprint.mission.discodeit.exception.channel;

public class DuplicateChannelNameException extends RuntimeException {
	public DuplicateChannelNameException() {
		super("이미 존재하는 채널명");
	}
}
