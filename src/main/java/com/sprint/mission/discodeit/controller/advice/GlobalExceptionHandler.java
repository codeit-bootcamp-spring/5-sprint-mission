package com.sprint.mission.discodeit.controller.advice;

import com.sprint.mission.discodeit.filter.RequestIdFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GlobalExceptionHandler {

    private static final String REQ_ID_HEADER = RequestIdFilter.HEADER;
    private static final String REQ_ID_ATTR = RequestIdFilter.ATTR;

    private ResponseEntity<ApiError> build(
        String code,
        String message,
        Map<String, Object> details,
        Throwable exception,
        HttpStatus httpStatus,
        HttpServletRequest req
    ) {
        String requestId = (String) req.getAttribute(REQ_ID_ATTR);
        ApiError body = ApiError.of(code, message, details, exception, httpStatus, requestId);

        return ResponseEntity.status(httpStatus)
            .header(REQ_ID_HEADER, requestId != null ? requestId : "")
            .contentType(MediaType.APPLICATION_JSON)
            .body(body);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleInvalidJson(
        HttpMessageNotReadableException e,
        HttpServletRequest req
    ) {
        String causeMsg = Optional.of(e.getMostSpecificCause())
            .map(Throwable::getMessage)
            .orElse(e.getMessage());

        Map<String, Object> details = new HashMap<>();
        if (causeMsg != null && !causeMsg.isBlank()) {
            details.put("cause", causeMsg);
        }
        details.put("path", req.getRequestURI());
        details.put("method", req.getMethod());

        log.warn("400(INVALID_JSON) {} {} -> {}", req.getMethod(), req.getRequestURI(), causeMsg);

        return build(
            "INVALID_JSON",
            "Unable to read request body, please check JSON format and field type",
            details,
            e,
            HttpStatus.BAD_REQUEST,
            req
        );
    }


    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolation(ConstraintViolationException e,
        HttpServletRequest req) {
        List<Map<String, Object>> violations = e.getConstraintViolations().stream()
            .map(cv -> Map.of(
                "property", propertyPath(cv),
                "message", cv.getMessage(),
                "invalid", cv.getInvalidValue()
            ))
            .toList();

        Map<String, Object> details = new HashMap<>();
        details.put("violations", violations);
        details.put("path", req.getRequestURI());
        details.put("method", req.getMethod());

        log.warn("400(INVALID_PARAMETER_VALUE) {} {} -> {}", req.getMethod(), req.getRequestURI(),
            violations);

        return build(
            "INVALID_PARAMETER_VALUE",
            "Request parameter value not valid",
            details,
            e,
            HttpStatus.BAD_REQUEST,
            req
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException e,
        HttpServletRequest req) {
        List<Map<String, Object>> fieldErrors = e.getBindingResult().getFieldErrors().stream()
            .map(fe -> Map.of(
                "field", fe.getField(),
                "message", fe.getDefaultMessage(),
                "rejected", fe.getRejectedValue()
            ))
            .toList();

        List<Map<String, String>> globalErrors = e.getBindingResult().getGlobalErrors().stream()
            .map(ge -> Map.of(
                "object", ge.getObjectName(),
                "message", ge.getDefaultMessage()
            ))
            .toList();

        Map<String, Object> details = new HashMap<>();
        details.put("fieldErrors", fieldErrors);
        if (!globalErrors.isEmpty()) {
            details.put("globalErrors", globalErrors);
        }
        details.put("path", req.getRequestURI());
        details.put("method", req.getMethod());

        log.warn("400(INVALID_BODY_VALUE) {} {} -> fields={}, globals={}",
            req.getMethod(), req.getRequestURI(), fieldErrors, globalErrors);

        return build(
            "INVALID_BODY_VALUE",
            "Request body value not valid",
            details,
            e,
            HttpStatus.BAD_REQUEST,
            req
        );
    }
    //
    // @ExceptionHandler(ParameterNumberNotValidException.class)
    // @ResponseStatus(HttpStatus.BAD_REQUEST)
    // public ApiError handleParameterNumberValidation(ParameterNumberNotValidException e,
    //     HttpServletRequest req) {
    //     String msg = (e.getMessage() != null && !e.getMessage().isBlank())
    //         ? e.getMessage()
    //         : "Multiple parameters not allowed";
    //
    //     List<String> params = e.getReceivedParameters();
    //     List<String> details = (params != null && !params.isEmpty())
    //         ? List.of(String.join(", ", params) + ": 하나만 포함하여야 합니다")
    //         : List.of();
    //
    //     log.warn("400(INVALID_PARAMETER_NUMBER) {} {} -> {}",
    //         req.getMethod(),
    //         req.getRequestURI(),
    //         msg
    //     );
    //
    //     return ApiError.from(req, HttpStatus.BAD_REQUEST, "INVALID_PARAMETER_NUMBER", msg, details);
    // }
    //
    // @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    // @ResponseStatus(HttpStatus.BAD_REQUEST)
    // public ApiError handleParameterTypeValidation(MethodArgumentTypeMismatchException e,
    //     HttpServletRequest req) {
    //     String detail = "parameter=%s, value=%s, expectedType=%s".formatted(
    //         e.getName(),
    //         e.getValue(),
    //         (e.getRequiredType() != null ? e.getRequiredType().getSimpleName() : "unknown")
    //     );
    //     log.warn("400(INVALID_PARAMETER_TYPE): {}", detail);
    //     return ApiError.from(req, HttpStatus.BAD_REQUEST, "INVALID_PARAMETER_TYPE",
    //         "Request parameter type not valid",
    //         List.of(detail));
    // }
    //
    // @ExceptionHandler(MissingServletRequestParameterException.class)
    // @ResponseStatus(HttpStatus.BAD_REQUEST)
    // public ApiError handleMissingParameter(MissingServletRequestParameterException e,
    //     HttpServletRequest req) {
    //     String detail = "missing parameter: %s (required type: %s)".formatted(
    //         e.getParameterName(),
    //         e.getParameterType()
    //     );
    //     log.warn("400(MISSING_PARAMETER): {}", detail);
    //     return ApiError.from(req, HttpStatus.BAD_REQUEST, "MISSING_PARAMETER",
    //         "Required parameter missing",
    //         List.of(detail));
    // }
    //
    // @ExceptionHandler(MissingServletRequestPartException.class)
    // @ResponseStatus(HttpStatus.BAD_REQUEST)
    // public ApiError handleMissingPart(MissingServletRequestPartException e,
    //     HttpServletRequest req) {
    //     String detail = "missing part: " + e.getRequestPartName();
    //     log.warn("400(MISSING_PART): {}", detail);
    //     return ApiError.from(req, HttpStatus.BAD_REQUEST, "MISSING_PART",
    //         "Required part missing",
    //         List.of(detail));
    // }
    //
    // @ExceptionHandler(UnauthorizedException.class)
    // @ResponseStatus(HttpStatus.UNAUTHORIZED)
    // public ApiError handleUnauthorized(UnauthorizedException e, HttpServletRequest req) {
    //     String msg = (e.getMessage() != null) ? e.getMessage() : "Invalid credentials";
    //     log.warn("401(UNAUTHORIZED) {} {} -> {}", req.getMethod(), req.getRequestURI(), msg);
    //     return ApiError.from(req, HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", msg, List.of());
    // }
    //
    // @ExceptionHandler(AccessDeniedException.class)
    // @ResponseStatus(HttpStatus.FORBIDDEN)
    // public ApiError handleForbidden(AccessDeniedException e, HttpServletRequest req) {
    //     String msg = (e.getMessage() != null) ? e.getMessage() : "Access denied";
    //     log.warn("403(FORBIDDEN) {} {} -> {}", req.getMethod(), req.getRequestURI(), msg);
    //     return ApiError.from(req, HttpStatus.FORBIDDEN, "FORBIDDEN", msg, List.of());
    // }
    //
    // @ExceptionHandler({ NoHandlerFoundException.class, NoResourceFoundException.class })
    // @ResponseStatus(HttpStatus.NOT_FOUND)
    // public ApiError handleNoHandler(Exception e, HttpServletRequest req) {
    //     log.warn("404(ENDPOINT_NOT_FOUND) {} {} -> {}", req.getMethod(), req.getRequestURI(),
    //         e.getMessage());
    //     return ApiError.from(req, HttpStatus.NOT_FOUND, "ENDPOINT_NOT_FOUND",
    //         "Endpoint not found", List.of());
    // }
    //
    // @ExceptionHandler(NotFoundException.class)
    // @ResponseStatus(HttpStatus.NOT_FOUND)
    // public ApiError handleNotFound(NotFoundException e, HttpServletRequest req) {
    //     String msg = (e.getMessage() != null) ? e.getMessage() : "Resource not found";
    //     log.warn("404(RESOURCE_NOT_FOUND) {} {} -> {}", req.getMethod(), req.getRequestURI(), msg);
    //     return ApiError.from(req, HttpStatus.NOT_FOUND, "RESOURCE_NOT_FOUND", msg, List.of());
    // }
    //
    // @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    // public ResponseEntity<ApiError> handleMethodNotAllowed(HttpRequestMethodNotSupportedException e,
    //     HttpServletRequest req) {
    //     String allowed =
    //         (e.getSupportedHttpMethods() == null || e.getSupportedHttpMethods().isEmpty())
    //             ? ""
    //             : e.getSupportedHttpMethods().stream().map(HttpMethod::name)
    //                 .collect(Collectors.joining(", "));
    //     log.warn("405(METHOD_NOT_ALLOWED) {} {} -> allowed: {}", req.getMethod(),
    //         req.getRequestURI(),
    //         allowed);
    //
    //     ApiError body = ApiError.from(
    //         req,
    //         HttpStatus.METHOD_NOT_ALLOWED,
    //         "METHOD_NOT_ALLOWED",
    //         "HTTP method %s not allowed".formatted(e.getMethod()),
    //         List.of()
    //     );
    //
    //     ResponseEntity.BodyBuilder builder = ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED);
    //     if (!allowed.isBlank()) {
    //         builder.header("Allow", allowed);
    //     }
    //
    //     return builder.body(body);
    // }
    //
    // @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    // @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    // public ApiError handleNotAcceptable(HttpMediaTypeNotAcceptableException e,
    //     HttpServletRequest req) {
    //     String supported = !e.getSupportedMediaTypes().isEmpty()
    //         ? " Supported types: " + e.getSupportedMediaTypes().stream()
    //         .map(Object::toString)
    //         .collect(Collectors.joining(", "))
    //         : "";
    //     String msg = "Media type not acceptable." + supported;
    //     log.warn("406(NOT_ACCEPTABLE) {} {} -> {}", req.getMethod(), req.getRequestURI(), msg);
    //     return ApiError.from(req, HttpStatus.NOT_ACCEPTABLE, "NOT_ACCEPTABLE", msg, List.of());
    // }
    //
    // @ExceptionHandler(DataIntegrityViolationException.class)
    // @ResponseStatus(HttpStatus.CONFLICT)
    // public ApiError handleDataIntegrity(DataIntegrityViolationException e, HttpServletRequest req) {
    //     String msg = Optional.ofNullable(sliceKeyToBracket(e.getMessage()))
    //         .orElse("Data integrity violation");
    //
    //     log.warn("409(CONFLICT) {} {} -> {}", req.getMethod(), req.getRequestURI(), msg);
    //     return ApiError.from(req, HttpStatus.CONFLICT, "CONFLICT", msg, List.of());
    // }
    //
    // private String sliceKeyToBracket(String message) {
    //     if (message == null) {
    //         return null;
    //     }
    //     int start = message.indexOf("Key ");
    //     if (start < 0) {
    //         return null;
    //     }
    //     int end = message.indexOf(']', start);
    //     if (end < 0) {
    //         return null;
    //     }
    //     String s = message.substring(start + 3, end).strip();
    //     return s.isEmpty() ? null : s;
    // }
    //
    // @ExceptionHandler(MaxUploadSizeExceededException.class)
    // @ResponseStatus(HttpStatus.PAYLOAD_TOO_LARGE)
    // public ApiError handleMaxSizeExceeded(MaxUploadSizeExceededException e,
    //     HttpServletRequest req) {
    //     String msg = (e.getMessage() != null) ? e.getMessage() : "File size exceeds limit";
    //     log.warn("413(PAYLOAD_TOO_LARGE) {} {} -> {}", req.getMethod(), req.getRequestURI(), msg);
    //     return ApiError.from(req, HttpStatus.PAYLOAD_TOO_LARGE, "PAYLOAD_TOO_LARGE", msg,
    //         List.of());
    // }
    //
    // @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    // @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    // public ApiError handleMediaTypeNotSupported(HttpMediaTypeNotSupportedException e,
    //     HttpServletRequest req) {
    //     String supported = !e.getSupportedMediaTypes().isEmpty()
    //         ? " Supported types: " + e.getSupportedMediaTypes().stream()
    //         .map(Object::toString)
    //         .collect(Collectors.joining(", "))
    //         : "";
    //     String msg = "Media type not allowed." + supported;
    //     log.warn("415(UNSUPPORTED_MEDIA_TYPE) {} {} -> {}", req.getMethod(), req.getRequestURI(),
    //         e.getMessage());
    //     return ApiError.from(req, HttpStatus.UNSUPPORTED_MEDIA_TYPE, "UNSUPPORTED_MEDIA_TYPE", msg,
    //         listOfNullable(e.getMessage()));
    // }
    //
    // @ExceptionHandler(Exception.class)
    // @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    // public ApiError handleAny(Exception e, HttpServletRequest req) {
    //     log.error("500(INTERNAL_ERROR) {} {} -> {}", req.getMethod(), req.getRequestURI(),
    //         e.toString(),
    //         e);
    //     return ApiError.from(req, HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR",
    //         "Unexpected error occurred", List.of());
    // }
    //
    // private static List<String> constraintErrors(Set<ConstraintViolation<?>> violations) {
    //     if (violations == null || violations.isEmpty()) {
    //         return List.of();
    //     }
    //     List<String> out = new ArrayList<>(violations.size());
    //     for (ConstraintViolation<?> v : violations) {
    //         String path =
    //             v.getPropertyPath() == null ? null : lastNode(v.getPropertyPath().toString());
    //         String reason = v.getMessage();
    //         out.add((path != null ? path + ": " : "") + reason);
    //     }
    //     return out;
    // }
    //
    // private static String lastNode(String propertyPath) {
    //     if (propertyPath == null || propertyPath.isBlank()) {
    //         return null;
    //     }
    //     int idx = propertyPath.lastIndexOf('.');
    //     return (idx >= 0) ? propertyPath.substring(idx + 1) : propertyPath;
    // }
    //
    // private static List<String> listOfNullable(String s) {
    //     return (s == null || s.isBlank()) ? List.of() : List.of(s);
    // }

    private static String propertyPath(ConstraintViolation<?> cv) {
        return cv.getPropertyPath() != null ? cv.getPropertyPath().toString() : "";
    }
}
