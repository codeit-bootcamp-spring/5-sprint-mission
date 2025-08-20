package com.sprint.mission.discodeit.exception;

import com.sprint.mission.discodeit.dto.ExceptionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ExceptionResponse> handleAllExceptions(Exception ex) {
    // TODO 나중에 예외 종류 별 세부처리
    return new ResponseEntity<>(
        ExceptionResponse.builder().code(500).message(ex.getMessage()).build(),
        HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
