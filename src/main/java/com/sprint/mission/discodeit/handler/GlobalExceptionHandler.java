package com.sprint.mission.discodeit.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice // 전체 Controller의 예외를 처리하는 클래스
public class GlobalExceptionHandler {

    // ✅ IllegalArgumentException 예외를 처리
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST); // 400 반환
    }

    // ✅ 모든 일반 예외 처리 (위에 안걸린 모든 Exception들)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGenericException(Exception ex, WebRequest request) {
        return buildErrorResponse("서버 내부 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR); // 500 반환
    }

    // ✅ 응답을 JSON 형태로 만들어주는 메서드
    private ResponseEntity<Object> buildErrorResponse(String message, HttpStatus status) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", Instant.now()); // 현재 시간
        body.put("status", status.value());   // HTTP 상태 코드
        body.put("error", status.getReasonPhrase()); // 상태 텍스트 (Bad Request, Internal Server Error와 같은 것들)
        body.put("message", message); // 실제 예외 메시지

        return new ResponseEntity<>(body, status);
    }
}
