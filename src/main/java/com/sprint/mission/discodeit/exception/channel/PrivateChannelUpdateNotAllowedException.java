package com.sprint.mission.discodeit.exception.channel;

import java.util.Map;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class PrivateChannelUpdateNotAllowedException extends ChannelException {
	public PrivateChannelUpdateNotAllowedException(Map<String, Object> details) {
		super(ErrorCode.PRIVATE_CHANNEL_UPDATE_NOT_ALLOWED, details);
	}

	public PrivateChannelUpdateNotAllowedException() {
		super(ErrorCode.PRIVATE_CHANNEL_UPDATE_NOT_ALLOWED);
	}
}
