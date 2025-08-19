package com.sprint.mission.discodeit.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ApiError> handleNotFound(
            NoSuchElementException e, HttpServletRequest req
    ) {
        ApiError body = ApiError.of(
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                e.getMessage(),
                req.getRequestURI(),
                List.of());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleBadRequest(
            Exception e, HttpServletRequest req
    ) {
        ApiError body = ApiError.of(
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                e.getMessage(),
                req.getRequestURI(),
                List.of()
        );
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiError> handleConflict(
            IllegalStateException e, HttpServletRequest req
    ) {
        ApiError body = ApiError.of(
                HttpStatus.CONFLICT.value(),
                "Conflict",
                e.getMessage(),
                req.getRequestURI(),
                List.of());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleException(
            Exception e, HttpServletRequest req
    ) {
        ApiError body = ApiError.of(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                "예상되지 않은 오류가 발생했습니다.",
                req.getRequestURI(),
                List.of());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
