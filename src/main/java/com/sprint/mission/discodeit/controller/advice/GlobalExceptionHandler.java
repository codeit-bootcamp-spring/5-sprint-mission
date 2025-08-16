package com.sprint.mission.discodeit.controller.advice;

import com.sprint.mission.discodeit.exception.AccessDeniedException;
import com.sprint.mission.discodeit.exception.DuplicateResourceException;
import com.sprint.mission.discodeit.exception.NotFoundException;
import com.sprint.mission.discodeit.exception.ValidatorsValidationException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public ResponseEntity<ApiError> handleBinding(BindException e, HttpServletRequest req) {
        List<String> details = bindingErrors(e);
        log.warn("400(BINDING) {} {} -> {}", req.getMethod(), req.getRequestURI(), details);
        return build(HttpStatus.BAD_REQUEST, "BAD_REQUEST", "요청이 올바르지 않습니다.", details);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolation(ConstraintViolationException e, HttpServletRequest req) {
        List<String> details = constraintErrors(e);
        log.warn("400(CONSTRAINT) {} {} -> {}", req.getMethod(), req.getRequestURI(), details);
        return build(HttpStatus.BAD_REQUEST, "BAD_REQUEST", "요청이 올바르지 않습니다.", details);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleNotReadable(HttpMessageNotReadableException e, HttpServletRequest req) {
        String msg = "요청 본문을 읽을 수 없습니다. JSON 형식과 필드 타입을 확인하세요.";
        e.getMostSpecificCause();
        log.warn("400(NOT_READABLE) {} {} -> {}", req.getMethod(), req.getRequestURI(),
                e.getMostSpecificCause().getMessage());
        return build(HttpStatus.BAD_REQUEST, "BAD_REQUEST", msg, List.of());
    }

    @ExceptionHandler({
            ValidatorsValidationException.class,
            IllegalArgumentException.class,
            MethodArgumentTypeMismatchException.class,
            MissingServletRequestParameterException.class
    })
    public ResponseEntity<ApiError> handleBadRequest(Exception e, HttpServletRequest req) {
        String reason = e.getMessage() != null ? e.getMessage() : "요청이 올바르지 않습니다.";
        log.warn("400(BAD_REQUEST) {} {} -> {}", req.getMethod(), req.getRequestURI(), reason);
        return build(HttpStatus.BAD_REQUEST, "BAD_REQUEST", "요청이 올바르지 않습니다.", List.of(reason));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(NotFoundException e, HttpServletRequest req) {
        String msg = e.getMessage() != null ? e.getMessage() : "리소스를 찾을 수 없습니다.";
        log.warn("404 {} {} -> {}", req.getMethod(), req.getRequestURI(), msg);
        return build(HttpStatus.NOT_FOUND, "NOT_FOUND", msg, List.of());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleForbidden(AccessDeniedException e, HttpServletRequest req) {
        String msg = e.getMessage() != null ? e.getMessage() : "접근이 거부되었습니다.";
        log.warn("403 {} {} -> {}", req.getMethod(), req.getRequestURI(), msg);
        return build(HttpStatus.FORBIDDEN, "FORBIDDEN", msg, List.of());
    }

    @ExceptionHandler({DuplicateResourceException.class, DataIntegrityViolationException.class, IllegalStateException.class})
    public ResponseEntity<ApiError> handleConflict(Exception e, HttpServletRequest req) {
        String msg = (e instanceof DuplicateResourceException && e.getMessage() != null)
                ? e.getMessage()
                : "리소스 충돌이 발생했습니다.";
        log.warn("409 {} {} -> {}", req.getMethod(), req.getRequestURI(), e.getMessage());
        return build(HttpStatus.CONFLICT, "CONFLICT", msg, List.of());
    }

    @ExceptionHandler({NoHandlerFoundException.class, NoResourceFoundException.class})
    public ResponseEntity<ApiError> handleNoHandler(Exception e, HttpServletRequest req) {
        var methodAndPath = req.getMethod() + " " + req.getRequestURI();
        var body = new ApiError("NOT_FOUND", "요청한 엔드포인트를 찾을 수 없습니다.", List.of(methodAndPath));
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiError> handleMethodNotAllowed(HttpRequestMethodNotSupportedException e,
                                                           HttpServletRequest req) {
        var allowed = e.getSupportedHttpMethods() != null ? e.getSupportedHttpMethods().toString() : "N/A";
        var detail = req.getMethod() + " " + req.getRequestURI() + " (allowed: " + allowed + ")";
        var body = new ApiError("METHOD_NOT_ALLOWED", "허용되지 않은 메서드입니다.", List.of(detail));
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpected(Exception e, HttpServletRequest req) {
        log.error("500 {} {} -> {}", req.getMethod(), req.getRequestURI(), e.toString(), e);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", "서버 오류가 발생했습니다.", List.of());
    }

    private static List<String> bindingErrors(BindException ex) {
        return ex.getBindingResult().getAllErrors().stream()
                .map(err -> (err instanceof FieldError fe)
                        ? fe.getField() + ": " + fe.getDefaultMessage()
                        : err.getDefaultMessage())
                .toList();
    }

    private static List<String> constraintErrors(ConstraintViolationException ex) {
        return ex.getConstraintViolations().stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .toList();
    }

    private static ResponseEntity<ApiError> build(HttpStatus status, String code, String message, List<String> details) {
        ApiError body = new ApiError(code, message, details);
        return ResponseEntity.status(status).body(body);
    }
}
