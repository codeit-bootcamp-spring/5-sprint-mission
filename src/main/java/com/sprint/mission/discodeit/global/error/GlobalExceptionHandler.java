package com.sprint.mission.discodeit.global.error;

import com.sprint.mission.discodeit.common.domain.exception.DiscodeitException;
import com.sprint.mission.discodeit.common.domain.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestCookieException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(DiscodeitException.class)
    public ResponseEntity<ErrorResponse> handleDiscodeitException(
        DiscodeitException exception,
        HttpServletRequest request
    ) {
        return buildResponse(
            exception.getErrorCode(),
            exception.getDetails(),
            exception,
            request
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(
        MethodArgumentNotValidException exception,
        HttpServletRequest request
    ) {
        Map<String, Object> details = new HashMap<>();
        details.put("fieldErrors", extractFieldErrors(exception.getBindingResult()));
        details.put("globalErrors", extractGlobalErrors(exception.getBindingResult()));

        return buildResponse(
            ErrorCode.INVALID_BODY_VALUE,
            details,
            exception,
            request
        );
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(
        ConstraintViolationException exception,
        HttpServletRequest request
    ) {
        List<Map<String, Object>> violations = exception.getConstraintViolations().stream()
            .map(cv -> Map.of(
                "property", cv.getPropertyPath() != null ? cv.getPropertyPath().toString() : "",
                "message", cv.getMessage(),
                "invalid", cv.getInvalidValue()
            ))
            .toList();

        return buildResponse(
            ErrorCode.INVALID_PARAMETER_VALUE,
            Map.of("violations", violations),
            exception,
            request
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleInvalidJson(
        HttpMessageNotReadableException exception,
        HttpServletRequest request
    ) {
        String cause = exception.getMostSpecificCause().getMessage();

        return buildResponse(
            ErrorCode.INVALID_JSON,
            Map.of("cause", cause),
            exception,
            request
        );
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParameter(
        MissingServletRequestParameterException ex,
        HttpServletRequest request
    ) {
        String message = String.format(
            "필수 파라미터 누락: %s (%s)", ex.getParameterName(), ex.getParameterType());
        return buildResponse(ErrorCode.MISSING_PARAMETER, message, Map.of(), ex, request);
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<ErrorResponse> handleMissingPart(
        MissingServletRequestPartException ex,
        HttpServletRequest request
    ) {
        String message = "필수 파트 누락: " + ex.getRequestPartName();
        return buildResponse(ErrorCode.MISSING_PART, message, Map.of(), ex, request);
    }

    @ExceptionHandler(MissingRequestCookieException.class)
    public ResponseEntity<ErrorResponse> handleMissingCookie(
        MissingRequestCookieException ex,
        HttpServletRequest request
    ) {
        return buildResponse(
            ErrorCode.MISSING_COOKIE, Map.of("cookieName", ex.getCookieName()), ex, request);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(
        MethodArgumentTypeMismatchException ex,
        HttpServletRequest request
    ) {
        String requiredType = (ex.getRequiredType() != null)
            ? ex.getRequiredType().getSimpleName()
            : "Unknown";
        String message = String.format(
            "파라미터 타입 불일치: %s (값: %s, 기대타입: %s)",
            ex.getName(), ex.getValue(), requiredType);
        return buildResponse(ErrorCode.INVALID_PARAMETER_VALUE, message, Map.of(), ex, request);
    }

    @ExceptionHandler({NoHandlerFoundException.class, NoResourceFoundException.class})
    public ResponseEntity<ErrorResponse> handleNotFound(Exception ex, HttpServletRequest request) {
        return buildResponse(ErrorCode.ENDPOINT_NOT_FOUND, Map.of(), ex, request);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotAllowed(
        HttpRequestMethodNotSupportedException ex, HttpServletRequest request
    ) {
        return buildResponse(ErrorCode.METHOD_NOT_ALLOWED, Map.of(), ex, request);
    }

    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    public ResponseEntity<ErrorResponse> handleNotAcceptable(
        HttpMediaTypeNotAcceptableException ex, HttpServletRequest request
    ) {
        return buildResponse(ErrorCode.NOT_ACCEPTABLE, Map.of(), ex, request);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleUnsupportedMediaType(
        HttpMediaTypeNotSupportedException ex, HttpServletRequest request
    ) {
        return buildResponse(ErrorCode.UNSUPPORTED_MEDIA_TYPE, Map.of(), ex, request);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrity(
        DataIntegrityViolationException exception, HttpServletRequest request
    ) {
        return buildResponse(
            ErrorCode.CONFLICT,
            Map.of("detail", exception.getMostSpecificCause().getMessage()),
            exception,
            request
        );
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAuthorizationDenied(
        AuthorizationDeniedException e, HttpServletRequest request
    ) {
        return buildResponse(ErrorCode.FORBIDDEN, Map.of(), e, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAny(Exception e, HttpServletRequest request) {
        return buildResponse(ErrorCode.INTERNAL_SERVER_ERROR, Map.of(), e, request);
    }

    private ResponseEntity<ErrorResponse> buildResponse(
        ErrorCode errorCode,
        Map<String, Object> details,
        Exception exception,
        HttpServletRequest request
    ) {
        return buildResponse(errorCode, errorCode.getMessage(), details, exception, request);
    }

    private ResponseEntity<ErrorResponse> buildResponse(
        ErrorCode errorCode,
        String message,
        Map<String, Object> details,
        Exception exception,
        HttpServletRequest request
    ) {
        Map<String, Object> mutableDetails = new HashMap<>(details != null ? details : Map.of());
        mutableDetails.put("path", request.getRequestURI());

        logException(errorCode, message, exception, request);

        ErrorResponse response = ErrorResponse.of(
            errorCode.name(),
            message,
            mutableDetails,
            exception,
            errorCode.getHttpStatus()
        );

        return ResponseEntity
            .status(errorCode.getHttpStatus())
            .contentType(MediaType.APPLICATION_JSON)
            .body(response);
    }

    private void logException(ErrorCode errorCode, String message, Exception e, HttpServletRequest request) {
        String logMsg = String.format(
            "[%s] %s %s : %s",
            errorCode.name(), request.getMethod(), request.getRequestURI(), message
        );

        if (errorCode.getHttpStatus().is4xxClientError()) {
            log.warn(logMsg);
        } else {
            log.error(logMsg, e);
        }
    }

    private List<Map<String, Object>> extractFieldErrors(BindingResult bindingResult) {
        return bindingResult.getFieldErrors().stream()
            .map(err -> {
                Map<String, Object> map = new HashMap<>();
                map.put("field", err.getField());
                map.put("message", err.getDefaultMessage());
                map.put("rejectedValue", err.getRejectedValue());
                return map;
            })
            .toList();
    }

    private List<Map<String, Object>> extractGlobalErrors(BindingResult bindingResult) {
        return bindingResult.getGlobalErrors().stream()
            .map(err -> {
                Map<String, Object> map = new HashMap<>();
                map.put("object", err.getObjectName());
                map.put("message", err.getDefaultMessage());
                return map;
            })
            .toList();
    }
}
