package com.sprint.mission.discodeit.exception.readStatus;

import java.util.Map;

import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.channel.ChannelException;

public class ReadStatusDuplicateException extends ChannelException {
	public ReadStatusDuplicateException(Map<String, Object> details) {
		super(ErrorCode.READ_STATUS_NOT_FOUND, details);
	}

	public ReadStatusDuplicateException() {
		super(ErrorCode.READ_STATUS_NOT_FOUND);
	}
}
