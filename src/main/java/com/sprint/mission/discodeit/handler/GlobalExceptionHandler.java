package com.sprint.mission.discodeit.handler;

import com.sprint.mission.discodeit.dto.common.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

/*Controller or Service : 문제 발생시 예외 던짐
 * GlobalExceptionHandler : 던져진 예외를 받아 클라이언트에게 적절한 응답 반환
 */

@ControllerAdvice // 모든 컨트롤러에서 발생하는 예외 가로채는 역할
public class GlobalExceptionHandler {

  //IllegalArgumentException 예외 처리
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
      IllegalArgumentException ex, WebRequest request) {
    String path = request.getDescription(false).replace("uri=", "");
    return buildErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST, path); // 400 반환
  }

  //Exception.class (위에 안걸린 모든 Exception들 처리)
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, WebRequest request) {
    ex.printStackTrace();
    String path = request.getDescription(false).replace("uri=", "");
    return buildErrorResponse("서버 내부 오류가 발생했습니다.",
        HttpStatus.INTERNAL_SERVER_ERROR, path); // 500 반환
  }

  //ErrorResponse DTO를 사용
  private ResponseEntity<ErrorResponse> buildErrorResponse(
      String message, HttpStatus status, String path) {

    ErrorResponse errorResponse = ErrorResponse.of(
        status.value(),
        status.getReasonPhrase(),
        message,
        path
    );

    return new ResponseEntity<>(errorResponse, status);
  }
}