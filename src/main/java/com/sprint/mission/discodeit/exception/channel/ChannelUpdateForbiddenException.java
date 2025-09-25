package com.sprint.mission.discodeit.exception.channel;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class ChannelUpdateForbiddenException extends ChannelException {
	public ChannelUpdateForbiddenException() {
		super(ErrorCode.CHANNEL_UPDATE_FORBIDDEN);
	}

	public static ChannelUpdateForbiddenException withId(String channelId) {
		ChannelUpdateForbiddenException exception = new ChannelUpdateForbiddenException();
		exception.addDetails("channel id", channelId);
		return exception;
	}
}
