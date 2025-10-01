package com.sprint.mission.discodeit.exception.message;

import java.util.Map;

import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.channel.ChannelException;

public class MessageNotFoundException extends ChannelException {
	public MessageNotFoundException(Map<String, Object> details) {
		super(ErrorCode.MESSAGE_NOT_FOUND, details);
	}

	public MessageNotFoundException() {
		super(ErrorCode.MESSAGE_NOT_FOUND);
	}
}
