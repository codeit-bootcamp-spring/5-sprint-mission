package com.sprint.mission.discodeit.exception.userstatus;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class UserStatusAlreadyExistsException extends UserStatusException {
	public UserStatusAlreadyExistsException() {
		super(ErrorCode.DUPLICATE_USER_STATUS);
	}

	public static UserStatusAlreadyExistsException withUserIds(String userId) {
		UserStatusAlreadyExistsException exception = new UserStatusAlreadyExistsException();
		exception.addDetails("userId", userId);
		return exception;
	}

	public static UserStatusAlreadyExistsException withIds(String statusId) {
		UserStatusAlreadyExistsException exception = new UserStatusAlreadyExistsException();
		exception.addDetails("userStatusId", statusId);
		return exception;
	}
}
