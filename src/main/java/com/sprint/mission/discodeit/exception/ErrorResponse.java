package com.sprint.mission.discodeit.exception;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public record ErrorResponse(Instant timestamp, String code, String message, Map<String, Object> details,
							String exceptionType, int status) {

	public ErrorResponse(DiscodeitException exception, int status) {
		this(Instant.now(), exception.getErrorCode().name(), exception.getMessage(), exception.getDetails(),
			exception.getClass().getSimpleName(), status);
	}

	public ErrorResponse(Exception exception, int status) {
		this(Instant.now(), exception.getClass().getSimpleName(), exception.getMessage(), new HashMap<>(),
			exception.getClass().getSimpleName(), status);
	}

}
