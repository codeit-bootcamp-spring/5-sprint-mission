package com.sprint.mission.discodeit.exception;

import java.util.UUID;

public class ChannelNotFoundException extends RuntimeException {
	public ChannelNotFoundException(String message) {
		super(message);
	}

	public ChannelNotFoundException(UUID channelId) {
		super("찾을 수 없는 채널" + channelId);
	}
}
