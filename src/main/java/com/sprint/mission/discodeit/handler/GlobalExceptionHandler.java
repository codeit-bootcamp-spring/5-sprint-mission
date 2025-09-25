package com.sprint.mission.discodeit.handler;

import com.sprint.mission.discodeit.dto.response.ErrorResponse;
import com.sprint.mission.discodeit.dto.response.ErrorResponse.FieldError;
import com.sprint.mission.discodeit.exception.binarycontent.FileNotFoundException;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.DuplicateChannelException;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.readstatus.ReadStatusNotFoundException;
import com.sprint.mission.discodeit.exception.user.DuplicateUserException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

/*Controller or Service : 문제 발생시 예외 던짐
 * GlobalExceptionHandler : 던져진 예외를 받아 클라이언트에게 적절한 응답 반환
 */

@RestControllerAdvice // 전체 애플리케이션에서 발생하는 예외를 자동으로 가로챔
public class GlobalExceptionHandler {

  //@Valid, @RequestBody 검증 실패시
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex,
      WebRequest request) {

    //예외 발생 경로(요청 URL 추출)
    String path = request.getDescription(false);
    //검증 실패한 필드별 에러 리스트 추출
    List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
        //각 필드와 검증 실패 메세지 묶어 객체 만들기
        .map(error -> new ErrorResponse.FieldError(error.getField(), error.getDefaultMessage()))
        .toList();
    //전체 에러 응답 객체 생성
    ErrorResponse errorResponse = ErrorResponse.of(
        HttpStatus.BAD_REQUEST.value(),
        HttpStatus.BAD_REQUEST.getReasonPhrase(),
        "입력값이 올바르지 않습니다.", //공통 에러 메세지
        path,
        fieldErrors //필드별 에러 메세지
    );
    return ResponseEntity.badRequest().body(errorResponse);
  }


  //IllegalArgumentException 예외 처리
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
      IllegalArgumentException ex, WebRequest request) {
    return buildErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST, request);
  }

  //Exception.class (나머지 Exception들 처리)
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, WebRequest request) {
    ex.printStackTrace();
    return buildErrorResponse("서버 내부 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, request);
  }

  /* 커스텀 예외 처리 부분들
   * User,Channel,Message,ReadStatus, BinaryContent
   */

  // USER 예외
  @ExceptionHandler(UserNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException ex,
      WebRequest request) {
    return buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND, request);
  }

  @ExceptionHandler(DuplicateUserException.class)
  public ResponseEntity<ErrorResponse> handleDuplicateUser(DuplicateUserException ex,
      WebRequest request) {
    return buildErrorResponse(ex.getMessage(), HttpStatus.CONFLICT, request);
  }

  // CHANNEL 예외
  @ExceptionHandler(ChannelNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleChannelNotFound(ChannelNotFoundException ex,
      WebRequest request) {
    return buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND, request);
  }

  @ExceptionHandler(DuplicateChannelException.class)
  public ResponseEntity<ErrorResponse> handleDuplicateChannel(DuplicateChannelException ex,
      WebRequest request) {
    return buildErrorResponse(ex.getMessage(), HttpStatus.CONFLICT, request);
  }

  // MESSAGE 예외
  @ExceptionHandler(MessageNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleMessageNotFound(MessageNotFoundException ex,
      WebRequest request) {
    return buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND, request);
  }

  // BINARY CONTENT 예외
  @ExceptionHandler(FileNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleFileNotFound(FileNotFoundException ex,
      WebRequest request) {
    return buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND, request);
  }

  // READSTATUS 예외
  @ExceptionHandler(ReadStatusNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleReadStatusNotFound(ReadStatusNotFoundException ex,
      WebRequest request) {
    return buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND, request);
  }

  // ErrorResponse 생성 (request 그대로 사용)
  private ResponseEntity<ErrorResponse> buildErrorResponse(String message, HttpStatus status,
      WebRequest request) {
    String path = request.getDescription(false);
    ErrorResponse errorResponse = ErrorResponse.of(
        status.value(),
        status.getReasonPhrase(),
        message,
        path
    );
    return new ResponseEntity<>(errorResponse, status);
  }
}