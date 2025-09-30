package com.sprint.mission.discodeit.advice;

import static com.sprint.mission.discodeit.exception.ErrorCode.INTERNAL_SERVER_ERROR;
import static com.sprint.mission.discodeit.exception.ErrorCode.*;
import static org.springframework.http.HttpStatus.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.ErrorResponse;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleException(Exception e) {
		// Log the eception (you can use a logging framework like SLF4J)
		log.error("Unexpected Error 발생", e);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorResponse.builder()
		  .timestamp(Instant.now())
		  .code(INTERNAL_SERVER_ERROR.name())
		  .message("Unexpected Error 발생: " + e.getMessage())
		  .details(Map.of())
		  .exceptionType(e.getClass().getSimpleName())
		  .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
		  .build());
	}

	@ExceptionHandler(DiscodeitException.class)
	public ResponseEntity<ErrorResponse> handleNoSuchElementException(DiscodeitException e) {
		log.error("DiscodeitException Error 발생", e);

		HttpStatus status = determineHttpStatus(e);
		// 에러 메시지 추출 및 응답 생성
		return ResponseEntity.status(status).body(ErrorResponse.builder()
		  .timestamp(e.getTimestamp())
		  .code(e.getErrorCode().name())
		  .message(e.getMessage())
		  .details(e.getDetails())
		  .exceptionType(e.getClass().getSimpleName())
		  .status(status.value())
		  .build());
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleValidationExceptions(
	  MethodArgumentNotValidException e) {
		log.error("요청 유효성 검사 실패: {}", e.getMessage());

		Map<String, Object> validationErrorsDetail = new HashMap<>();
		e.getBindingResult().getAllErrors().forEach(error -> {
			String fieldName = ((FieldError)error).getField();
			String errorMessage = error.getDefaultMessage();
			validationErrorsDetail.put(fieldName, errorMessage);
		});

		return ResponseEntity
		  .status(BAD_REQUEST)
		  .body(ErrorResponse.builder()
			.timestamp(Instant.now())
			.code(VALIDATION_ERROR.name())
			.message(VALIDATION_ERROR.getMessage())
			.details(validationErrorsDetail)
			.exceptionType(e.getClass().getSimpleName())
			.status(BAD_REQUEST.value())
			.build());
	}

	private HttpStatus determineHttpStatus(DiscodeitException exception) {
		ErrorCode errorCode = exception.getErrorCode();

		return switch (errorCode) {
			case USER_NOT_FOUND,
				 CHANNEL_NOT_FOUND,
				 USER_STATUS_NOT_FOUND,
				 BINARY_CONTENT_NOT_FOUND,
				 AUTHOR_NOT_FOUND,
				 MESSAGE_NOT_FOUND,
				 READ_STATUS_NOT_FOUND -> NOT_FOUND;

			case DUPLICATE_USERNAME,
				 DUPLICATE_USER_EMAIL,
				 INVALID_USER_PARAM,
				 INVALID_REQUEST,
				 PRIVATE_CHANNEL_UPDATE_NOT_ALLOWED,
				 PARTICIPANTS_EMPTY,
				 READ_STATUS_DUPLICATE,
				 DUPLICATE_USERNAME_OR_EMAIL,
				 VALIDATION_ERROR,
				 WRONG_PASSWORD -> BAD_REQUEST;

			case INVALID_USER_CREDENTIALS -> UNAUTHORIZED;

			case INTERNAL_SERVER_ERROR,
				 URI_CREATE_FAIL,
				 SAVE_TO_FILE_STORAGE_FAIL,
				 BINARY_CONTENT_READ_FAIL -> HttpStatus.INTERNAL_SERVER_ERROR;
		};
	}
}
