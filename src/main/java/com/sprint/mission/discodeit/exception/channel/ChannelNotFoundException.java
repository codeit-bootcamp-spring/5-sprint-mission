package com.sprint.mission.discodeit.exception.channel;

public class ChannelNotFoundException extends RuntimeException {
	public ChannelNotFoundException() {
		super("찾을 수 없는 채널");
	}
}
