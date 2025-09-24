package com.sprint.mission.discodeit.exception.channel;

import java.util.Map;

import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.server.ServerException;

public class ChannelNotFoundException extends ChannelException {
	public ChannelNotFoundException(Map<String, Object> details) {
		super(ErrorCode.CHANNEL_NOT_FOUND, details);
	}

	public ChannelNotFoundException() {
		super(ErrorCode.CHANNEL_NOT_FOUND);
	}
}
