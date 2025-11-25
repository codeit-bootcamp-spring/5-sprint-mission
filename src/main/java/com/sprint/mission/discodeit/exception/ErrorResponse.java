package com.sprint.mission.discodeit.exception;

import java.time.Instant;
import java.util.Collections;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class ErrorResponse {
	private final Instant timestamp;
	private final String code;
	private final String message;
	private final Map<String, Object> details;
	private final String exceptionType;
	private final Integer status;

	public static ErrorResponse of(ErrorCode code, int status, Exception e) {
		return new ErrorResponse(
		  Instant.now(),
		  code.name(),
		  code.getMessage(),
		  Collections.emptyMap(),
		  e.getClass().getSimpleName(),
		  status
		);
	}

	public static ErrorResponse of(DiscodeitException e, int status) {
		ErrorCode errorCode = e.getErrorCode();
		return new ErrorResponse(
		  Instant.now(),
		  errorCode.name(),
		  errorCode.getMessage(),
		  Collections.emptyMap(),
		  e.getClass().getSimpleName(),
		  status
		);
	}
}
