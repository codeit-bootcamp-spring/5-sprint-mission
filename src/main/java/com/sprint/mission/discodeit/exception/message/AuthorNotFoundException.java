package com.sprint.mission.discodeit.exception.message;

import java.util.Map;

import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.channel.ChannelException;

public class AuthorNotFoundException extends ChannelException {
	public AuthorNotFoundException(Map<String, Object> details) {
		super(ErrorCode.AUTHOR_NOT_FOUND, details);
	}

	public AuthorNotFoundException() {
		super(ErrorCode.AUTHOR_NOT_FOUND);
	}
}
