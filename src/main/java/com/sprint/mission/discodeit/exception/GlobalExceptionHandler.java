package com.sprint.mission.discodeit.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
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
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.springframework.util.StringUtils.hasText;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(DiscodeitException.class)
    public ResponseEntity<ErrorResponse> handleDiscodeitException(
        DiscodeitException exception,
        HttpServletRequest request
    ) {
        return createResponse(
            exception.getErrorCode(),
            exception.getErrorCode().getMessage(),
            exception.getDetails(),
            exception,
            request
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleInvalidJson(
        HttpMessageNotReadableException exception,
        HttpServletRequest request
    ) {
        ErrorCode errorCode = ErrorCode.INVALID_JSON;

        String cause = Optional.of(exception.getMostSpecificCause())
            .map(Throwable::getMessage)
            .orElse(exception.getMessage());

        Map<String, Object> details = new HashMap<>();
        if (hasText(cause)) {
            details.put("cause", cause);
        }

        return createResponse(
            errorCode,
            errorCode.getMessage(),
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
        ErrorCode errorCode = ErrorCode.INVALID_PARAMETER_VALUE;

        List<Map<String, Object>> violations = exception.getConstraintViolations().stream()
            .map(cv -> Map.of(
                "property", (cv.getPropertyPath() != null ? cv.getPropertyPath().toString() : ""),
                "message", cv.getMessage(),
                "invalid", cv.getInvalidValue()
            ))
            .toList();

        Map<String, Object> details = new HashMap<>();
        details.put("violations", violations);

        String logMessage = "violations=%s".formatted(violations);

        return createResponse(
            errorCode,
            errorCode.getMessage(),
            logMessage,
            details,
            exception,
            request
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
        MethodArgumentNotValidException exception,
        HttpServletRequest request
    ) {
        ErrorCode errorCode = ErrorCode.INVALID_BODY_VALUE;

        List<Map<String, Object>> fieldErrors = getFieldErrors(exception.getBindingResult());
        List<Map<String, Object>> globalErrors = getGlobalErrors(exception.getBindingResult());

        Map<String, Object> details = new HashMap<>();
        details.put("fieldErrors", fieldErrors);
        if (!globalErrors.isEmpty()) {
            details.put("globalErrors", globalErrors);
        }

        String logMessage = "fieldErrors=%s, globalErrors=%s".formatted(fieldErrors, globalErrors);

        return createResponse(
            errorCode,
            errorCode.getMessage(),
            logMessage,
            details,
            exception,
            request
        );
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleParameterTypeValidation(
        MethodArgumentTypeMismatchException exception,
        HttpServletRequest request
    ) {
        ErrorCode errorCode = ErrorCode.INVALID_PARAMETER_VALUE;

        String message = "%s: parameter=%s, value=%s, expectedType=%s".formatted(
            errorCode.getMessage(),
            exception.getName(),
            exception.getValue(),
            (exception.getRequiredType() != null ? exception.getRequiredType().getSimpleName() : "unknown")
        );

        return createResponse(
            errorCode,
            message,
            Map.of(),
            exception,
            request
        );
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParameter(
        MissingServletRequestParameterException exception,
        HttpServletRequest request
    ) {
        ErrorCode errorCode = ErrorCode.MISSING_PARAMETER;

        String message = "%s: %s (필요한 매개변수: %s)".formatted(
            errorCode.getMessage(),
            exception.getParameterName(),
            exception.getParameterType()
        );

        return createResponse(
            errorCode,
            message,
            Map.of(),
            exception,
            request
        );
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<ErrorResponse> handleMissingPart(
        MissingServletRequestPartException exception,
        HttpServletRequest request
    ) {
        ErrorCode errorCode = ErrorCode.MISSING_PART;

        String message = errorCode.getMessage() + exception.getRequestPartName();

        return createResponse(
            errorCode,
            message,
            Map.of(),
            exception,
            request
        );
    }

    @ExceptionHandler(MissingRequestCookieException.class)
    public ResponseEntity<ErrorResponse> handleMissingCookie(
        MissingRequestCookieException exception,
        HttpServletRequest request
    ) {
        ErrorCode errorCode = ErrorCode.MISSING_COOKIE;

        String message = "%s: %s".formatted(errorCode.getMessage(), exception.getCookieName());

        return createResponse(
            errorCode,
            message,
            Map.of("cookieName", exception.getCookieName()),
            exception,
            request
        );
    }

    @ExceptionHandler({NoHandlerFoundException.class, NoResourceFoundException.class})
    public ResponseEntity<ErrorResponse> handleNoHandler(
        Exception exception,
        HttpServletRequest request
    ) {
        ErrorCode errorCode = ErrorCode.ENDPOINT_NOT_FOUND;

        return createResponse(
            errorCode,
            errorCode.getMessage(),
            exception.getMessage(),
            Map.of(),
            exception,
            request
        );
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotAllowed(
        HttpRequestMethodNotSupportedException exception,
        HttpServletRequest request
    ) {
        ErrorCode errorCode = ErrorCode.METHOD_NOT_ALLOWED;

        String message = errorCode.getMessage() + ": %s".formatted(exception.getMethod());

        String allowed =
            (exception.getSupportedHttpMethods() == null || exception.getSupportedHttpMethods().isEmpty())
                ? ""
                : exception.getSupportedHttpMethods().stream()
                .map(HttpMethod::name)
                .collect(Collectors.joining(", "));

        Map<String, Object> details = new HashMap<>();
        if (!allowed.isBlank()) {
            details.put("allowed", allowed);
        }

        ResponseEntity<ErrorResponse> body = createResponse(
            errorCode,
            message,
            details,
            exception,
            request
        );

        if (!allowed.isBlank()) {
            return ResponseEntity.status(errorCode.getHttpStatus())
                .headers(body.getHeaders())
                .header(HttpHeaders.ALLOW, allowed)
                .body(body.getBody());
        }

        return body;
    }

    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    public ResponseEntity<ErrorResponse> handleNotAcceptable(
        HttpMediaTypeNotAcceptableException exception,
        HttpServletRequest request
    ) {
        ErrorCode errorCode = ErrorCode.NOT_ACCEPTABLE;

        String supported = !exception.getSupportedMediaTypes().isEmpty()
            ? " 지원되는 타입들: " + exception.getSupportedMediaTypes().stream()
            .map(Object::toString)
            .collect(Collectors.joining(", "))
            : "";
        String message = errorCode.getMessage() + supported;

        return createResponse(
            errorCode,
            message,
            Map.of(),
            exception,
            request
        );
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrity(
        DataIntegrityViolationException exception,
        HttpServletRequest request
    ) {
        ErrorCode errorCode = ErrorCode.CONFLICT;
        String logMessage = Optional.ofNullable(sliceKeyToBracket(exception.getMessage()))
            .orElse(errorCode.getMessage());

        return createResponse(
            errorCode,
            errorCode.getMessage(),
            logMessage,
            Map.of(),
            exception,
            request
        );
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponse> handleMaxSizeExceeded(
        MaxUploadSizeExceededException exception,
        HttpServletRequest request
    ) {
        ErrorCode errorCode = ErrorCode.PAYLOAD_TOO_LARGE;

        return createResponse(
            errorCode,
            errorCode.getMessage(),
            exception.getMessage(),
            Map.of(),
            exception,
            request
        );
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMediaTypeNotSupported(
        HttpMediaTypeNotSupportedException exception,
        HttpServletRequest request
    ) {
        ErrorCode errorCode = ErrorCode.UNSUPPORTED_MEDIA_TYPE;

        String supported = !exception.getSupportedMediaTypes().isEmpty()
            ? " 지원되는 타입들: " + exception.getSupportedMediaTypes().stream()
            .map(Object::toString)
            .collect(Collectors.joining(", "))
            : "";
        String message = errorCode.getMessage() + supported;

        return createResponse(
            errorCode,
            message,
            Map.of(),
            exception,
            request
        );
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAuthorizationDenied(
        Exception exception,
        HttpServletRequest request
    ) {
        String requiredRole = extractRequiredRole(exception);
        ErrorCode errorCode;
        String message;
        Map<String, Object> details = new HashMap<>();

        if (requiredRole != null) {
            errorCode = ErrorCode.INSUFFICIENT_ROLE;
            message = "%s 권한이 필요합니다.".formatted(requiredRole.toLowerCase());
            details.put("requiredRole", requiredRole);
        } else {
            errorCode = ErrorCode.FORBIDDEN;
            message = errorCode.getMessage();
        }

        return createResponse(
            errorCode,
            message,
            details,
            exception,
            request
        );
    }

    private String extractRequiredRole(Exception exception) {
        String message = exception.getMessage();
        if (message == null) {
            return null;
        }

        // @PreAuthorize("hasRole('ROLE_NAME')") 패턴에서 역할 추출
        Pattern pattern = Pattern.compile(
            "hasRole\\(['\"](?:ROLE_)?([^'\"]+)['\"]\\)"
        );
        Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            return matcher.group(1);
        }

        return null;
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAny(
        Exception exception,
        HttpServletRequest request
    ) {
        return createResponse(
            ErrorCode.INTERNAL_SERVER_ERROR,
            ErrorCode.INTERNAL_SERVER_ERROR.getMessage(),
            Collections.emptyMap(),
            exception,
            request
        );
    }

    private ResponseEntity<ErrorResponse> createResponse(
        ErrorCode errorCode,
        String message,
        Map<String, Object> details,
        Exception exception,
        HttpServletRequest request
    ) {
        return createResponse(
            errorCode,
            message,
            message,
            details,
            exception,
            request
        );
    }

    private ResponseEntity<ErrorResponse> createResponse(
        ErrorCode errorCode,
        String message,
        String logMessage,
        Map<String, Object> details,
        Exception exception,
        HttpServletRequest request
    ) {
        Map<String, Object> mutableDetails = new HashMap<>(details);
        mutableDetails.put("path", request.getRequestURI());
        mutableDetails.put("method", request.getMethod());
        if (request.getQueryString() != null) {
            mutableDetails.put("query", request.getQueryString());
        }

        log(
            errorCode.getHttpStatus(),
            errorCode.name(),
            logMessage,
            exception,
            request
        );

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

    private void log(
        HttpStatus status,
        String code,
        String message,
        Exception exception,
        HttpServletRequest request
    ) {
        String logMessage = String.format("%s(%s) %s %s -> %s",
            status, code, request.getMethod(), request.getRequestURI(), message);

        if (status.is4xxClientError()) {
            log.warn(logMessage);
        } else {
            log.error(logMessage, exception);
        }
    }

    private List<Map<String, Object>> getFieldErrors(BindingResult bindingResult) {
        return bindingResult.getFieldErrors().stream()
            .map(error -> {
                Map<String, Object> map = new HashMap<>();
                map.put("field", error.getField());
                map.put("message", error.getDefaultMessage());
                map.put("rejectedValue", error.getRejectedValue());
                return map;
            })
            .collect(Collectors.toList());
    }

    private List<Map<String, Object>> getGlobalErrors(BindingResult bindingResult) {
        return bindingResult.getGlobalErrors().stream()
            .map(error -> {
                Map<String, Object> map = new HashMap<>();
                map.put("object", error.getObjectName());
                map.put("message", error.getDefaultMessage());
                return map;
            })
            .collect(Collectors.toList());
    }

    private String sliceKeyToBracket(String message) {
        if (message == null) {
            return null;
        }
        int start = message.indexOf("Key ");
        if (start < 0) {
            return null;
        }
        int end = message.indexOf(']', start);
        if (end < 0) {
            return null;
        }
        String s = message.substring(start + 3, end).strip();
        return s.isEmpty() ? null : s;
    }
}
