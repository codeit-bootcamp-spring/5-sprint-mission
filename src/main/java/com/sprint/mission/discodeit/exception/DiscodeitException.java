package com.sprint.mission.discodeit.exception;

import java.time.Instant;
import java.util.Map;

import lombok.Getter;

@Getter
public class DiscodeitException extends RuntimeException {
	private final Instant timestamp;
	private final ErrorCode errorCode;
	private final Map<String, Object> details;

	public DiscodeitException(ErrorCode code, Map<String, Object> details) {
		super(code.getMessage());
		this.timestamp = Instant.now();
		this.errorCode = code;
		this.details = details;
	}

	public DiscodeitException(ErrorCode code) {
		super(code.getMessage());
		this.timestamp = Instant.now();
		this.errorCode = code;
		this.details = Map.of();

	}

}
