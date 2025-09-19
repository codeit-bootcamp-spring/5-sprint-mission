package com.sprint.mission.discodeit.exception;

import java.util.NoSuchElementException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
