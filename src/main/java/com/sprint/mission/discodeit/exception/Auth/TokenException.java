package com.sprint.mission.discodeit.exception.Auth;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;

public class TokenException extends DiscodeitException {

	public TokenException(ErrorCode errorCode) {
		super(ErrorCode.INVALID_TOKEN);
	}

	public TokenException(ErrorCode errorCode, Throwable cause) {
		super(ErrorCode.INVALID_TOKEN, cause);
	}
}
