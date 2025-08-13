package com.sprint.mission.discodeit.controlleradvice;

import com.sprint.mission.discodeit.dto.error.ApiError;
import com.sprint.mission.discodeit.exception.AccessDeniedException;
import com.sprint.mission.discodeit.exception.DuplicateResourceException;
import com.sprint.mission.discodeit.exception.NotFoundException;
import com.sprint.mission.discodeit.exception.ValidatorsValidationException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({
            MethodArgumentNotValidException.class,
            BindException.class,
            ConstraintViolationException.class,
            HttpMessageNotReadableException.class,
            ValidatorsValidationException.class,
            IllegalArgumentException.class})
    public ResponseEntity<ApiError> handleBadRequest(Exception e) {
        List<String> errors = new ArrayList<>();
        if (e instanceof MethodArgumentNotValidException manve) {
            manve.getBindingResult().getAllErrors().forEach(err -> {
                if (err instanceof FieldError fe) errors.add(fe.getField() + ": " + fe.getDefaultMessage());
                else errors.add(err.getDefaultMessage());
            });
        } else if (e instanceof BindException be) {
            be.getBindingResult().getAllErrors().forEach(err -> {
                if (err instanceof FieldError fe) errors.add(fe.getField() + ": " + fe.getDefaultMessage());
                else errors.add(err.getDefaultMessage());
            });
        } else if (e instanceof ConstraintViolationException cve) {
            cve.getConstraintViolations().forEach(v -> errors.add(v.getPropertyPath() + ": " + v.getMessage()));
        } else {
            errors.add(e.getMessage());
        }
        ApiError body = new ApiError("BAD_REQUEST", "요청이 올바르지 않습니다.", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler({NotFoundException.class})
    public ResponseEntity<ApiError> handleNotFound(Exception e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiError("NOT_FOUND", e.getMessage(), List.of()));
    }

    @ExceptionHandler({AccessDeniedException.class})
    public ResponseEntity<ApiError> handleForbidden(Exception e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ApiError("FORBIDDEN", e.getMessage(), List.of()));
    }

    @ExceptionHandler({
            DuplicateResourceException.class,
            DataIntegrityViolationException.class,
            DataIntegrityViolationException.class,
            IllegalStateException.class})
    public ResponseEntity<ApiError> handleConflict(Exception e) {
        ApiError body = new ApiError("CONFLICT", e.getMessage() != null ? e.getMessage() : "리소스 충돌이 발생했습니다.", List.of());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpected(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiError("INTERNAL_ERROR", "서버 오류가 발생했습니다.", List.of()));
    }
}
