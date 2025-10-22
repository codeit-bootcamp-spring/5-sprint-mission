package com.codeit.mission.discodeit.exception;

import com.codeit.mission.discodeit.exception.binarycontent.BinaryContentException;
import com.codeit.mission.discodeit.exception.channel.ChannelException;
import com.codeit.mission.discodeit.exception.message.MessageException;
import com.codeit.mission.discodeit.exception.user.UserException;
import java.time.Instant;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e) {

        Map<String, Object> validationErrors = new LinkedHashMap<>();

        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            String field = fieldError.getField();
            String message = fieldError.getDefaultMessage();
            Object rejectedValue = fieldError.getRejectedValue();

            Map<String, Object> fieldErrorDetail = new HashMap<>();
            fieldErrorDetail.put("message", message);
            fieldErrorDetail.put("rejectedValue", rejectedValue);
            fieldErrorDetail.put("code", fieldError.getCode());

            validationErrors.put(field, fieldErrorDetail);
        }

        for (ObjectError globalError : e.getBindingResult().getGlobalErrors()) {
            String objectName = globalError.getObjectName();
            String message = globalError.getDefaultMessage();

            Map<String, Object> globalErrorDetail = new HashMap<>();
            globalErrorDetail.put("message", message);
            globalErrorDetail.put("code", globalError.getCode());

            validationErrors.put(objectName + "_error", globalErrorDetail);
        }

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .code("VALIDATION_ERROR")
                .message("입력 값 검증에 실패했습니다. 요청 데이터를 확인해주세요.")
                .details(validationErrors)
                .exceptionType("MethodArgumentNotValidException")
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(DiscodeitException.class)
    public ResponseEntity<ErrorResponse> handleDiscodeitException(DiscodeitException e) {
        HttpStatus status = determineHttpStatus(e);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(e.getTimestamp())
                .status(status.value())
                .code(e.getErrorCode().name())
                .message(e.getMessage())
                .details(e.getDetails().isEmpty() ? null : e.getDetails())
                .exceptionType(e.getClass().getSimpleName())
                .build();

        return ResponseEntity.status(status).body(errorResponse);
    }

    @ExceptionHandler(UserException.class)
    public ResponseEntity<ErrorResponse> handleUserException(UserException e) {
        return handleDiscodeitException(e);
    }

    @ExceptionHandler(ChannelException.class)
    public ResponseEntity<ErrorResponse> handleChannelException(ChannelException e) {
        return handleDiscodeitException(e);
    }

    @ExceptionHandler(MessageException.class)
    public ResponseEntity<ErrorResponse> handleMessageException(MessageException e) {
        return handleDiscodeitException(e);
    }

    @ExceptionHandler(BinaryContentException.class)
    public ResponseEntity<ErrorResponse> handleBinaryContentException(BinaryContentException e) {
        return handleDiscodeitException(e);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException e) {

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .code("INVALID_REQUEST")
                .message(e.getMessage())
                .exceptionType("IllegalArgumentException")
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorResponse> handleNoSuchElementException(NoSuchElementException e) {

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.NOT_FOUND.value())
                .code("NOT_FOUND")
                .message(e.getMessage())
                .exceptionType("NoSuchElementException")
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception e) {

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .code("INTERNAL_SERVER_ERROR")
                .message("내부 서버 오류가 발생했습니다.")
                .details(Map.of("exceptionType", e.getClass().getSimpleName()))
                .exceptionType(e.getClass().getSimpleName())
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    private HttpStatus determineHttpStatus(DiscodeitException e) {
        return switch (e.getErrorCode()) {
            case USER_NOT_FOUND, CHANNEL_NOT_FOUND, MESSAGE_NOT_FOUND,
                 BINARY_CONTENT_NOT_FOUND, READ_STATUS_NOT_FOUND, USER_STATUS_NOT_FOUND ->
                    HttpStatus.NOT_FOUND;
            case DUPLICATE_USERNAME, DUPLICATE_EMAIL, USER_ALREADY_EXISTS,
                 PRIVATE_CHANNEL_UPDATE, INVALID_PASSWORD, INVALID_CHANNEL_TYPE,
                 MESSAGE_CONTENT_EMPTY, MESSAGE_TOO_LONG, INVALID_FILE_TYPE,
                 READ_STATUS_ALREADY_EXISTS, USER_STATUS_ALREADY_EXISTS,
                 INVALID_REQUEST, VALIDATION_ERROR -> HttpStatus.BAD_REQUEST;
            case CHANNEL_ACCESS_DENIED, MESSAGE_ACCESS_DENIED, ACCESS_DENIED ->
                    HttpStatus.FORBIDDEN;
            case AUTHENTICATION_FAILED -> HttpStatus.UNAUTHORIZED;
            case FILE_SIZE_EXCEEDED -> HttpStatus.PAYLOAD_TOO_LARGE;
            case FILE_UPLOAD_FAILED, DATABASE_ERROR, INTERNAL_SERVER_ERROR ->
                    HttpStatus.INTERNAL_SERVER_ERROR;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }
}