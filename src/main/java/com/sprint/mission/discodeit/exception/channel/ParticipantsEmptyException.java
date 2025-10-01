package com.sprint.mission.discodeit.exception.channel;

import java.util.Map;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class ParticipantsEmptyException extends ChannelException {
	public ParticipantsEmptyException(Map<String, Object> details) {
		super(ErrorCode.PARTICIPANTS_EMPTY, details);
	}

	public ParticipantsEmptyException() {
		super(ErrorCode.PARTICIPANTS_EMPTY);
	}
}
