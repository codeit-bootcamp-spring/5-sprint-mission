package com.sprint.mission.discodeit.exception.readstatus;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class ReadStatusNotFoundException extends ReadStatusException {
	public ReadStatusNotFoundException() {
		super(ErrorCode.READ_STATUS_NOT_FOUND);
	}

	public static ReadStatusNotFoundException withIds(String readStatusId){
		ReadStatusNotFoundException exception = new ReadStatusNotFoundException();
		exception.addDetails("readStatusId", readStatusId);
		return exception;
	}
}
