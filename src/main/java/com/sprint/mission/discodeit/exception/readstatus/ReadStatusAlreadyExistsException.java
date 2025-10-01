package com.sprint.mission.discodeit.exception.readstatus;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class ReadStatusAlreadyExistsException extends ReadStatusException {
	public ReadStatusAlreadyExistsException() {
		super(ErrorCode.DUPLICATE_READ_STATUS);
	}

	public static ReadStatusAlreadyExistsException withIds(String userId, String channelId) {
		ReadStatusAlreadyExistsException exception = new ReadStatusAlreadyExistsException();
		exception.addDetails("userId", userId);
		exception.addDetails("channelId", channelId);
		return exception;
	}
}
