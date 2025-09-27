package com.sprint.mission.discodeit.exception;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleException(Exception e) {
		log.error("Exception occurred: {}", e.getMessage());
		ErrorResponse errorResponse = new ErrorResponse(e, 500);
		return ResponseEntity.status(errorResponse.status()).body(errorResponse);
	}

	@ExceptionHandler(DiscodeitException.class)
	public ResponseEntity<ErrorResponse> handleCustomException(DiscodeitException e) {
		log.error("DiscodeitException occurred: code={}, message={}", e.getErrorCode(), e.getMessage());
		ErrorResponse errorResponse = new ErrorResponse(e, determineHttpStatus(e).value());
		return ResponseEntity.status(errorResponse.status()).body(errorResponse);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException e) {
		log.error("유효성 검사 실패: {}", e.getMessage());

		Map<String, Object> validationErrors = new HashMap<>();
		e.getBindingResult().getAllErrors().forEach(error -> {
			String fieldName = ((FieldError)error).getField();
			String errorMessage = error.getDefaultMessage();
			validationErrors.put(fieldName, errorMessage);
		});

		ErrorResponse response = new ErrorResponse(
			Instant.now(),
			"VALIDATION_ERROR",
			"요청 데이터 유효성 검사에 실패했습니다",
			validationErrors,
			e.getClass().getSimpleName(),
			HttpStatus.BAD_REQUEST.value()
		);

		return ResponseEntity
			.status(HttpStatus.BAD_REQUEST)
			.body(response);
	}

	private HttpStatus determineHttpStatus(DiscodeitException exception) {
		ErrorCode errorCode = exception.getErrorCode();
		return switch (errorCode) {
			case USER_NOT_FOUND, READ_STATUS_NOT_FOUND, BINARY_CONTENT_NOT_FOUND, CHANNEL_NOT_FOUND,
				 MESSAGE_NOT_FOUND, FILE_NOT_FOUND -> HttpStatus.NOT_FOUND;
			case DUPLICATE_USER, DUPLICATE_USER_STATUS, DUPLICATE_READ_STATUS -> HttpStatus.CONFLICT;
			case INVALID_USER_CREDENTIALS -> HttpStatus.UNAUTHORIZED;
			case INVALID_REQUEST, INVALID_USER_PARAMETER, FILE_PROCESSING_FAIL -> HttpStatus.BAD_REQUEST;
			case INTERNAL_SERVER_ERROR -> HttpStatus.INTERNAL_SERVER_ERROR;
			case CHANNEL_UPDATE_FORBIDDEN -> HttpStatus.FORBIDDEN;
		};
	}
}
