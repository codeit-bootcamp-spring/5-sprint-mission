package com.sprint.mission.discodeit.controller.advice;

import com.sprint.mission.discodeit.exception.AccessDeniedException;
import com.sprint.mission.discodeit.exception.NotFoundException;
import com.sprint.mission.discodeit.exception.UnauthorizedException;
import com.sprint.mission.discodeit.filter.RequestIdFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
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
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GlobalExceptionHandler {

    private static final String REQ_ID_HEADER = RequestIdFilter.HEADER;
    private static final String REQ_ID_ATTR = RequestIdFilter.ATTR;

    private static String sliceKeyToBracket(String message) {
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

    private static String propertyPath(ConstraintViolation<?> cv) {
        return cv.getPropertyPath() != null ? cv.getPropertyPath().toString() : "";
    }

    private static void log(
        HttpStatus httpStatus,
        String code,
        HttpServletRequest req,
        String msg
    ) {
        log.warn("{}({}) {} {} -> {}", httpStatus, code, req.getMethod(), req.getRequestURI(), msg);
    }

    private ResponseEntity<ErrorResponse> build(
        String code,
        String message,
        Map<String, Object> details,
        Throwable exception,
        HttpStatus httpStatus,
        HttpServletRequest req
    ) {
        details.put("path", req.getRequestURI());
        details.put("method", req.getMethod());
        if (req.getQueryString() != null) {
            details.put("query", req.getQueryString());
        }

        String requestId = (String) req.getAttribute(REQ_ID_ATTR);
        ErrorResponse body = ErrorResponse.of(code, message, details, exception, httpStatus, requestId);

        return ResponseEntity.status(httpStatus)
            .header(REQ_ID_HEADER, requestId != null ? requestId : "")
            .contentType(MediaType.APPLICATION_JSON)
            .body(body);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleInvalidJson(
        HttpMessageNotReadableException e,
        HttpServletRequest req
    ) {
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        String code = "INVALID_JSON";
        String msg = "Unable to read request body, please check JSON format and field type";
        String causeMsg = Optional.of(e.getMostSpecificCause())
            .map(Throwable::getMessage)
            .orElse(e.getMessage());

        Map<String, Object> details = new HashMap<>();
        if (causeMsg != null && !causeMsg.isBlank()) {
            details.put("cause", causeMsg);
        }

        log(httpStatus, code, req, causeMsg);

        return build(code, msg, details, e, httpStatus, req);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(
        ConstraintViolationException e,
        HttpServletRequest req
    ) {
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        String code = "INVALID_PARAMETER_VALUE";
        String msg = "Request parameter value not valid";
        List<Map<String, Object>> violations = e.getConstraintViolations().stream()
            .map(cv -> Map.of(
                "property", propertyPath(cv),
                "message", cv.getMessage(),
                "invalid", cv.getInvalidValue()
            ))
            .toList();

        Map<String, Object> details = new HashMap<>();
        details.put("violations", violations);

        log(httpStatus, code, req, violations.toString());

        return build(code, msg, details, e, httpStatus, req);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
        MethodArgumentNotValidException e,
        HttpServletRequest req
    ) {
        List<Map<String, Object>> fieldErrors = e.getBindingResult().getFieldErrors().stream()
            .map(fe -> {
                Map<String, Object> m = new HashMap<>();
                m.put("field", fe.getField());
                m.put("message", fe.getDefaultMessage());
                m.put("rejected", fe.getRejectedValue());
                return m;
            })
            .toList();

        List<Map<String, String>> globalErrors = e.getBindingResult().getGlobalErrors().stream()
            .map(ge -> {
                Map<String, String> m = new HashMap<>();
                m.put("object", ge.getObjectName());
                m.put("message", ge.getDefaultMessage());
                return m;
            })
            .toList();

        Map<String, Object> details = new HashMap<>();
        details.put("fieldErrors", fieldErrors);
        if (!globalErrors.isEmpty()) {
            details.put("globalErrors", globalErrors);
        }

        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        String code = "INVALID_BODY_VALUE";
        String msg = "Request body value not valid";

        log.warn("400({}) {} {} -> code, fields={}, globals={}", code,
            req.getMethod(), req.getRequestURI(), fieldErrors, globalErrors);

        return build(code, msg, details, e, httpStatus, req);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleParameterTypeValidation(
        MethodArgumentTypeMismatchException e,
        HttpServletRequest req
    ) {
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        String code = "INVALID_PARAMETER_TYPE";
        String msg = "parameter=%s, value=%s, expectedType=%s".formatted(
            e.getName(),
            e.getValue(),
            (e.getRequiredType() != null ? e.getRequiredType().getSimpleName() : "unknown")
        );

        log(httpStatus, code, req, msg);

        return build(code, msg, new HashMap<>(), e, httpStatus, req);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParameter(
        MissingServletRequestParameterException e,
        HttpServletRequest req
    ) {
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        String code = "MISSING_PARAMETER";
        String msg = "missing parameter: %s (required type: %s)".formatted(
            e.getParameterName(),
            e.getParameterType()
        );

        log(httpStatus, code, req, msg);

        return build(code, msg, new HashMap<>(), e, httpStatus, req);
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<ErrorResponse> handleMissingPart(
        MissingServletRequestPartException e,
        HttpServletRequest req
    ) {
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        String code = "MISSING_PART";
        String msg = "missing part: " + e.getRequestPartName();

        log(httpStatus, code, req, msg);

        return build(code, msg, new HashMap<>(), e, httpStatus, req);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorized(
        UnauthorizedException e,
        HttpServletRequest req
    ) {
        HttpStatus httpStatus = HttpStatus.UNAUTHORIZED;
        String code = "UNAUTHORIZED";
        String msg = (e.getMessage() != null) ? e.getMessage() : "Invalid credentials";

        log(httpStatus, code, req, msg);

        return build(code, msg, new HashMap<>(), e, httpStatus, req);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleForbidden(
        AccessDeniedException e,
        HttpServletRequest req
    ) {
        HttpStatus httpStatus = HttpStatus.FORBIDDEN;
        String code = "FORBIDDEN";
        String msg = (e.getMessage() != null) ? e.getMessage() : "Access denied";

        log(httpStatus, code, req, msg);

        return build(code, msg, new HashMap<>(), e, httpStatus, req);
    }

    @ExceptionHandler({NoHandlerFoundException.class, NoResourceFoundException.class})
    public ResponseEntity<ErrorResponse> handleNoHandler(
        Exception e,
        HttpServletRequest req
    ) {
        HttpStatus httpStatus = HttpStatus.NOT_FOUND;
        String code = "NOT_FOUND";
        String msg = (e.getMessage() != null) ? e.getMessage() : "Endpoint not found";

        log(httpStatus, code, req, msg);

        return build(code, msg, new HashMap<>(), e, httpStatus, req);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(
        NotFoundException e,
        HttpServletRequest req
    ) {
        HttpStatus httpStatus = HttpStatus.NOT_FOUND;
        String code = "RESOURCE_NOT_FOUND";
        String msg = (e.getMessage() != null) ? e.getMessage() : "Resource not found";

        log(httpStatus, code, req, msg);

        return build(code, msg, new HashMap<>(), e, httpStatus, req);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotAllowed(
        HttpRequestMethodNotSupportedException e,
        HttpServletRequest req
    ) {
        HttpStatus httpStatus = HttpStatus.METHOD_NOT_ALLOWED;
        String code = "METHOD_NOT_ALLOWED";
        String msg = "Method not allowed: %s".formatted(e.getMethod());
        String allowed =
            (e.getSupportedHttpMethods() == null || e.getSupportedHttpMethods().isEmpty())
                ? ""
                : e.getSupportedHttpMethods().stream()
                .map(HttpMethod::name)
                .collect(Collectors.joining(", "));

        Map<String, Object> details = new HashMap<>();
        if (!allowed.isBlank()) {
            details.put("allowed", allowed);
        }

        log.warn("{}({}) {} {} -> {}, allowed: {}",
            httpStatus,
            code,
            req.getMethod(),
            req.getRequestURI(),
            msg,
            allowed
        );

        ResponseEntity<ErrorResponse> body = build(
            code,
            msg,
            details,
            e,
            httpStatus,
            req
        );

        if (!allowed.isBlank()) {
            return ResponseEntity.status(httpStatus)
                .headers(body.getHeaders())
                .header(HttpHeaders.ALLOW, allowed)
                .body(body.getBody());
        }

        return body;
    }

    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    public ResponseEntity<ErrorResponse> handleNotAcceptable(
        HttpMediaTypeNotAcceptableException e,
        HttpServletRequest req
    ) {
        HttpStatus httpStatus = HttpStatus.NOT_ACCEPTABLE;
        String code = "NOT_ACCEPTABLE";
        String supported = !e.getSupportedMediaTypes().isEmpty()
            ? " Supported types: " + e.getSupportedMediaTypes().stream()
            .map(Object::toString)
            .collect(Collectors.joining(", "))
            : "";
        String msg = "Media type not acceptable." + supported;

        log(httpStatus, code, req, msg);

        return build(code, msg, new HashMap<>(), e, httpStatus, req);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrity(
        DataIntegrityViolationException e,
        HttpServletRequest req
    ) {
        HttpStatus httpStatus = HttpStatus.CONFLICT;
        String code = "CONFLICT";
        String msg = Optional.ofNullable(sliceKeyToBracket(e.getMessage()))
            .orElse("Data integrity violation");

        log(httpStatus, code, req, msg);

        return build(code, msg, new HashMap<>(), e, httpStatus, req);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponse> handleMaxSizeExceeded(
        MaxUploadSizeExceededException e,
        HttpServletRequest req
    ) {
        HttpStatus httpStatus = HttpStatus.PAYLOAD_TOO_LARGE;
        String code = "PAYLOAD_TOO_LARGE";
        String msg = (e.getMessage() != null) ? e.getMessage() : "File size exceeds limit";

        log(httpStatus, code, req, msg);

        return build(code, msg, new HashMap<>(), e, httpStatus, req);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMediaTypeNotSupported(
        HttpMediaTypeNotSupportedException e,
        HttpServletRequest req
    ) {
        HttpStatus httpStatus = HttpStatus.UNSUPPORTED_MEDIA_TYPE;
        String code = "UNSUPPORTED_MEDIA_TYPE";
        String supported = !e.getSupportedMediaTypes().isEmpty()
            ? " Supported types: " + e.getSupportedMediaTypes().stream()
            .map(Object::toString)
            .collect(Collectors.joining(", "))
            : "";
        String msg = "Media type not allowed." + supported;

        log(httpStatus, code, req, msg);

        return build(code, msg, new HashMap<>(), e, httpStatus, req);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAny(
        Exception e,
        HttpServletRequest req
    ) {
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        String code = "INTERNAL_ERROR";
        String msg = "Unexpected error occurred";

        log(httpStatus, code, req, e.toString());

        return build(code, msg, new HashMap<>(), e, httpStatus, req);
    }
}
