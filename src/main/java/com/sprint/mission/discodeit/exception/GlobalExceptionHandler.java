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

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleException(Exception e) {
    ErrorResponse errorResponse = new ErrorResponse(e,500);
    return ResponseEntity
        .status(errorResponse.getStatus())
        .body(errorResponse);
  }

  @ExceptionHandler(DiscodeitException.class)
  public ResponseEntity<ErrorResponse> handleCustomException(DiscodeitException e){
    HttpStatus httpStatus = deterlminHttpStatus(e);
    ErrorResponse errorResponse = new ErrorResponse(e,httpStatus.value());
    return ResponseEntity
        .status(errorResponse.getStatus())
        .body(errorResponse);
  }

  private HttpStatus deterlminHttpStatus(DiscodeitException exception) {
    ErrorCode errorCode = exception.getErrorCode();
    return switch(errorCode){
      case DUPLICATE_USER,READSTATUS_ALREADY_EXIST,BINARY_CONTENT_ALREADY_EXIST,
           USERSTATUS_ALREADY_EXIST
           ->  HttpStatus.CONFLICT;
      case USER_NOT_FOUND, USERSTATUS_NOT_FOUND,CHANNEL_NOT_FOUND,MESSAGE_NOT_FOUND,
           READSTATUS_NOT_FOUND,BINARY_CONTENT_NOT_FOUND
          ->   HttpStatus.NOT_FOUND;
      case INVALID_REQUEST-> HttpStatus.BAD_REQUEST;
      case INTERNAL_SERVER_ERROR -> HttpStatus.INTERNAL_SERVER_ERROR;
      case AUTH_WRONG_PASSWORD -> HttpStatus.UNAUTHORIZED;
      case PRIVATE_CHANNEL_UPDATE ->  HttpStatus.FORBIDDEN;
    };
  }

  //유효성 검사
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
    Map<String, Object> vaildationErrors = new HashMap<>();
    ex.getBindingResult().getAllErrors().forEach(error -> {
      String fieldName = ((FieldError)error).getField();
      String errorMessage = error.getDefaultMessage();
      vaildationErrors.put(fieldName,errorMessage);
    });
     ErrorResponse response = new ErrorResponse(
         Instant.now(),
         "VALIDATION_ERROR",
         "요청 데이터 유효석 검사에 실패했습니다.",
         vaildationErrors,
         ex.getClass().getSimpleName(),
         HttpStatus.BAD_REQUEST.value()
     );
     return ResponseEntity
        .status(response.getStatus())
        .body(response);
  }


}
