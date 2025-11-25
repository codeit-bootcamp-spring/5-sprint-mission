package com.sprint.mission.discodeit.exception.auth;

import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.user.UserException;

public class NotAuthenticationException extends UserException {

	public NotAuthenticationException() {
		super(ErrorCode.NOT_AUTHORIZED);
	}
}
