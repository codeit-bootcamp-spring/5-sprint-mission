package com.sprint.mission.discodeit.controller.advice;

import com.sprint.mission.discodeit.exception.AccessDeniedException;
import com.sprint.mission.discodeit.exception.DuplicateResourceException;
import com.sprint.mission.discodeit.exception.NotFoundException;
import com.sprint.mission.discodeit.exception.ParameterNumberNotValidException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(HttpMessageNotReadableException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ApiError handleInvalidJson(HttpMessageNotReadableException e, HttpServletRequest req) {
    e.getMostSpecificCause();
    String causeMsg = e.getMostSpecificCause().getMessage();
    log.warn("400(INVALID_JSON) {} {} -> {}", req.getMethod(), req.getRequestURI(), causeMsg);
    return ApiError.from(req, HttpStatus.BAD_REQUEST, "INVALID_JSON",
        "Unable to read request body, please check JSON format and field type",
        listOfNullable(causeMsg));
  }

  @ExceptionHandler(ConstraintViolationException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ApiError handleConstraintViolation(ConstraintViolationException e,
      HttpServletRequest req) {
    List<String> details = constraintErrors(e.getConstraintViolations());
    log.warn("400(INVALID_PARAMETER_VALUE) {} {} -> {}", req.getMethod(), req.getRequestURI(),
        details);
    return ApiError.from(req, HttpStatus.BAD_REQUEST, "INVALID_PARAMETER_VALUE",
        "Request parameter value not valid", details);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ApiError handleValidation(MethodArgumentNotValidException e, HttpServletRequest req) {
    List<String> details = e.getBindingResult().getFieldErrors().stream()
        .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
        .toList();
    log.warn("400(INVALID_BODY_VALUE) {} {} -> {}", req.getMethod(), req.getRequestURI(), details);
    return ApiError.from(req, HttpStatus.BAD_REQUEST, "INVALID_BODY_VALUE",
        "Request body value not valid", details);
  }

  @ExceptionHandler(ParameterNumberNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ApiError handleParameterNumberValidation(ParameterNumberNotValidException e,
      HttpServletRequest req) {
    String msg = (e.getMessage() != null && !e.getMessage().isBlank())
        ? e.getMessage()
        : "Multiple parameters not allowed";

    List<String> params = e.getReceivedParameters();
    List<String> details = (params != null && !params.isEmpty())
        ? List.of(String.join(", ", params) + ": 하나만 포함하여아 합니다")
        : List.of();

    log.warn("400(INVALID_PARAMETER_NUMBER) {} {} -> {}",
        req.getMethod(),
        req.getRequestURI(),
        msg
    );

    return ApiError.from(req, HttpStatus.BAD_REQUEST, "INVALID_PARAMETER_NUMBER", msg, details);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ApiError handleIllegalArgument(IllegalArgumentException e, HttpServletRequest req) {
    log.warn("400(ILLEGAL_ARGUMENT) {} {} -> {}", req.getMethod(), req.getRequestURI(),
        e.getMessage());
    return ApiError.from(req, HttpStatus.BAD_REQUEST, "ILLEGAL_ARGUMENT", e.getMessage(),
        List.of());
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ApiError handleParameterTypeValidation(MethodArgumentTypeMismatchException e,
      HttpServletRequest req) {
    String detail = "parameter=%s, value=%s, expectedType=%s".formatted(
        e.getName(),
        e.getValue(),
        (e.getRequiredType() != null ? e.getRequiredType().getSimpleName() : "unknown")
    );
    log.warn("400(INVALID_PARAMETER_TYPE): {}", detail);
    return ApiError.from(req, HttpStatus.BAD_REQUEST, "INVALID_PARAMETER_TYPE",
        "Request parameter type not valid",
        List.of(detail));
  }

  @ExceptionHandler(MissingServletRequestParameterException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ApiError handleMissingParameter(MissingServletRequestParameterException e,
      HttpServletRequest req) {
    String detail = "missing parameter: %s (required type: %s)".formatted(
        e.getParameterName(),
        e.getParameterType()
    );
    log.warn("400(MISSING_PARAMETER): {}", detail);
    return ApiError.from(req, HttpStatus.BAD_REQUEST, "MISSING_PARAMETER",
        "Required parameter missing",
        List.of(detail));
  }

  @ExceptionHandler(MissingServletRequestPartException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ApiError handleMissingPart(MissingServletRequestPartException e, HttpServletRequest req) {
    String detail = "missing part: " + e.getRequestPartName();
    log.warn("400(MISSING_PART): {}", detail);
    return ApiError.from(req, HttpStatus.BAD_REQUEST, "MISSING_PART",
        "Required part missing",
        List.of(detail));
  }

  @ExceptionHandler(AccessDeniedException.class)
  @ResponseStatus(HttpStatus.FORBIDDEN)
  public ApiError handleForbidden(AccessDeniedException e, HttpServletRequest req) {
    String msg = (e.getMessage() != null) ? e.getMessage() : "Access denied";
    log.warn("403(FORBIDDEN) {} {} -> {}", req.getMethod(), req.getRequestURI(), msg);
    return ApiError.from(req, HttpStatus.FORBIDDEN, "FORBIDDEN", msg, List.of());
  }

  @ExceptionHandler({NoHandlerFoundException.class, NoResourceFoundException.class})
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ApiError handleNoHandler(Exception e, HttpServletRequest req) {
    log.warn("404(ENDPOINT_NOT_FOUND) {} {} -> {}", req.getMethod(), req.getRequestURI(),
        e.getMessage());
    return ApiError.from(req, HttpStatus.NOT_FOUND, "ENDPOINT_NOT_FOUND",
        "Endpoint not found", List.of());
  }

  @ExceptionHandler(NotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ApiError handleNotFound(NotFoundException e, HttpServletRequest req) {
    String msg = (e.getMessage() != null) ? e.getMessage() : "Resource not found";
    log.warn("404(RESOURCE_NOT_FOUND) {} {} -> {}", req.getMethod(), req.getRequestURI(), msg);
    return ApiError.from(req, HttpStatus.NOT_FOUND, "RESOURCE_NOT_FOUND", msg, List.of());
  }

  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  public ResponseEntity<ApiError> handleMethodNotAllowed(HttpRequestMethodNotSupportedException e,
      HttpServletRequest req) {
    String allowed = (e.getSupportedHttpMethods() == null || e.getSupportedHttpMethods().isEmpty())
        ? ""
        : e.getSupportedHttpMethods().stream().map(HttpMethod::name)
            .collect(Collectors.joining(", "));
    log.warn("405(METHOD_NOT_ALLOWED) {} {} -> allowed: {}", req.getMethod(), req.getRequestURI(),
        allowed);

    ApiError body = ApiError.from(
        req,
        HttpStatus.METHOD_NOT_ALLOWED,
        "METHOD_NOT_ALLOWED",
        "HTTP method %s not allowed".formatted(e.getMethod()),
        List.of()
    );

    ResponseEntity.BodyBuilder builder = ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED);
    if (!allowed.isBlank()) {
      builder.header("Allow", allowed);
    }

    return builder.body(body);
  }

  @ExceptionHandler({DuplicateResourceException.class, DataIntegrityViolationException.class,
      IllegalStateException.class})
  @ResponseStatus(HttpStatus.CONFLICT)
  public ApiError handleConflict(Exception e, HttpServletRequest req) {
    String msg = (e instanceof DuplicateResourceException && e.getMessage() != null)
        ? e.getMessage()
        : "Resource already exists";
    log.warn("409(CONFLICT) {} {} -> {}", req.getMethod(), req.getRequestURI(), e.getMessage());
    return ApiError.from(req, HttpStatus.CONFLICT, "CONFLICT", msg, List.of());
  }

  @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
  @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
  public ApiError handleMediaTypeNotSupported(HttpMediaTypeNotSupportedException e,
      HttpServletRequest req) {
    String supported = !e.getSupportedMediaTypes().isEmpty()
        ? " Supported types: " + e.getSupportedMediaTypes().stream()
        .map(Object::toString)
        .collect(Collectors.joining(", "))
        : "";
    String msg = "Media type not allowed." + supported;
    log.warn("415(UNSUPPORTED_MEDIA_TYPE) {} {} -> {}", req.getMethod(), req.getRequestURI(),
        e.getMessage());
    return ApiError.from(req, HttpStatus.UNSUPPORTED_MEDIA_TYPE, "UNSUPPORTED_MEDIA_TYPE", msg,
        listOfNullable(e.getMessage()));
  }

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ApiError handleAny(Exception e, HttpServletRequest req) {
    log.error("500(INTERNAL_ERROR) {} {} -> {}", req.getMethod(), req.getRequestURI(), e.toString(),
        e);
    return ApiError.from(req, HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR",
        "Unexpected error occurred", List.of());
  }

  private static List<String> constraintErrors(Set<ConstraintViolation<?>> violations) {
    if (violations == null || violations.isEmpty()) {
      return List.of();
    }
    List<String> out = new ArrayList<>(violations.size());
    for (ConstraintViolation<?> v : violations) {
      String path = v.getPropertyPath() == null ? null : lastNode(v.getPropertyPath().toString());
      String reason = v.getMessage();
      out.add((path != null ? path + ": " : "") + reason);
    }
    return out;
  }

  private static String lastNode(String propertyPath) {
    if (propertyPath == null || propertyPath.isBlank()) {
      return null;
    }
    int idx = propertyPath.lastIndexOf('.');
    return (idx >= 0) ? propertyPath.substring(idx + 1) : propertyPath;
  }

  private static List<String> listOfNullable(String s) {
    return (s == null || s.isBlank()) ? List.of() : List.of(s);
  }
}
