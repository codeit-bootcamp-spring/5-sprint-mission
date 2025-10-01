package com.sprint.mission.discodeit.exception.readStatus;

import java.util.Map;

import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.channel.ChannelException;

public class ReadStatusNotFoundException extends ChannelException {
	public ReadStatusNotFoundException(Map<String, Object> details) {
		super(ErrorCode.READ_STATUS_NOT_FOUND, details);
	}

	public ReadStatusNotFoundException() {
		super(ErrorCode.READ_STATUS_NOT_FOUND);
	}
}
