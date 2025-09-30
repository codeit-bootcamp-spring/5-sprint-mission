package com.sprint.mission.discodeit.exception;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

//    @ExceptionHandler(IllegalArgumentException.class)
//    public ResponseEntity<String> handleException(IllegalArgumentException e) {
//        e.printStackTrace();
//        return ResponseEntity
//                .status(HttpStatus.BAD_REQUEST)
//                .body(e.getMessage());
//    }
//
//    @ExceptionHandler(NoSuchElementException.class)
//    public ResponseEntity<String> handleException(NoSuchElementException e) {
//        e.printStackTrace();
//        return ResponseEntity
//                .status(HttpStatus.NOT_FOUND)
//                .body(e.getMessage());
//    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("예상치 못한 오류 발생 : {}", e.getMessage(), e);
        ErrorResponse errorResponse = new ErrorResponse(e, 500);
        return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);
    }

    // 유효성 검사!!!
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        log.error("요청 유효성 검사 실패: {}", ex.getMessage());
        //검증 실패한 모든 에러를 순회하면서, 필드명 → 에러 메시지 형태로 validationErrors에 저장.
        Map<String, Object> validationErrors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            validationErrors.put(fieldName, errorMessage);
        });
        //발생 시각·에러 코드·메시지·에러 상세·예외 이름·HTTP 상태코드를 담음.
        ErrorResponse response = new ErrorResponse(
                Instant.now(),
                "VALIDATION_ERROR",
                "요청 데이터 유효성 검사에 실패했습니다",
                validationErrors,
                ex.getClass().getSimpleName(),
                HttpStatus.BAD_REQUEST.value()
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    // 커스텀 에러 처리
    @ExceptionHandler(DiscodeitException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(DiscodeitException e) {
        log.error("커스텀 예외 발생 : code={}, message={}", e.getErrorCode(), e.getMessage());
        HttpStatus httpStatus = determineHttpStatus(e);
        ErrorResponse errorResponse = new ErrorResponse(e, httpStatus.value());
        return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);
    }

    private HttpStatus determineHttpStatus(DiscodeitException exception) {
        ErrorCode errorCode = exception.getErrorCode();
        return switch (errorCode) {
            // User
            case USER_NOT_FOUND -> HttpStatus.NOT_FOUND;
            case DUPLICATE_USER -> HttpStatus.CONFLICT;
            case INVALID_USER_CREDENTIALS -> HttpStatus.UNAUTHORIZED;

            // Channel
            case CHANNEL_NOT_FOUND -> HttpStatus.NOT_FOUND;
            case PRIVATTE_CHANNEL_UPDATE -> HttpStatus.FORBIDDEN;         // 권한 없는 채널 접근
            case CHANNEL_ALREADY_EXISTS -> HttpStatus.CONFLICT;

            // Message
            case MESSAGE_NOT_FOUND -> HttpStatus.NOT_FOUND;
            case MESSAGE_SEND_FAILED -> HttpStatus.INTERNAL_SERVER_ERROR; // 서버 문제로 전송 실패
            case MESSAGE_UPDATE_NOT_ALLOWED -> HttpStatus.FORBIDDEN;      // 수정 권한 없음

            // File
            case FILE_NOT_FOUND -> HttpStatus.NOT_FOUND;
            case FILE_UPLOAD_FAILED -> HttpStatus.INTERNAL_SERVER_ERROR;  // 업로드 실패
            case FILE_DOWNLOAD_FAILED -> HttpStatus.INTERNAL_SERVER_ERROR;// 다운로드 실패
            case FILE_TOO_LARGE -> HttpStatus.PAYLOAD_TOO_LARGE;         // 413

            // 일반/기타
            case INTERNAL_SERVER_ERROR -> HttpStatus.INTERNAL_SERVER_ERROR;
            case INVALID_REQUEST, INVALID_USER_PARAMETER -> HttpStatus.BAD_REQUEST;

        };
    }
}
