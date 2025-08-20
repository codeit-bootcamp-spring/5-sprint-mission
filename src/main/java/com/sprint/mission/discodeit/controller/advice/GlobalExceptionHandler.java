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
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
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

    log.warn("400(INVALID_PARAMETER_NUMBER) {} {} -> {}", req.getMethod(), req.getRequestURI(),
        msg);
    return ApiError.from(req, HttpStatus.BAD_REQUEST, "INVALID_NUMBER_OF_PARAMETERS", msg, details);
  }

  @ExceptionHandler({
      IllegalArgumentException.class,
      MethodArgumentTypeMismatchException.class,
      MissingServletRequestParameterException.class
  })
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ApiError handleBadRequest(Exception e, HttpServletRequest req) {
    String reason = (e.getMessage() != null) ? e.getMessage() : "요청이 올바르지 않습니다.";
    log.warn("400(BAD_REQUEST) {} {} -> {}", req.getMethod(), req.getRequestURI(), reason);
    return ApiError.from(req, HttpStatus.BAD_REQUEST, "BAD_REQUEST",
        "요청이 올바르지 않습니다", List.of(reason));
  }

  @ExceptionHandler(AccessDeniedException.class)
  @ResponseStatus(HttpStatus.FORBIDDEN)
  public ApiError handleForbidden(AccessDeniedException e, HttpServletRequest req) {
    String msg = (e.getMessage() != null) ? e.getMessage() : "접근이 거부되었습니다.";
    log.warn("403(FORBIDDEN) {} {} -> {}", req.getMethod(), req.getRequestURI(), msg);
    return ApiError.from(req, HttpStatus.FORBIDDEN, "FORBIDDEN", msg, List.of());
  }

  @ExceptionHandler({NoHandlerFoundException.class, NoResourceFoundException.class})
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ApiError handleNoHandler(Exception e, HttpServletRequest req) {
    log.warn("404(NOT_FOUND_ENDPOINT) {} {} -> {}", req.getMethod(), req.getRequestURI(),
        e.getMessage());
    return ApiError.from(req, HttpStatus.NOT_FOUND, "NOT_FOUND",
        "요청한 엔드포인트를 찾을 수 없습니다.", List.of());
  }

  @ExceptionHandler(NotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ApiError handleNotFound(NotFoundException e, HttpServletRequest req) {
    String msg = (e.getMessage() != null) ? e.getMessage() : "리소스를 찾을 수 없습니다.";
    log.warn("404(NOT_FOUND) {} {} -> {}", req.getMethod(), req.getRequestURI(), msg);
    return ApiError.from(req, HttpStatus.NOT_FOUND, "NOT_FOUND", msg, List.of());
  }

  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
  public ApiError handleMethodNotAllowed(HttpRequestMethodNotSupportedException e,
      HttpServletRequest req) {
    String allowed = (e.getSupportedHttpMethods() == null || e.getSupportedHttpMethods().isEmpty())
        ? ""
        : e.getSupportedHttpMethods().stream().map(HttpMethod::name)
            .collect(Collectors.joining(", "));
    log.warn("405(METHOD_NOT_ALLOWED) {} {} -> allowed: {}", req.getMethod(), req.getRequestURI(),
        allowed);
    return ApiError.from(req, HttpStatus.METHOD_NOT_ALLOWED, "METHOD_NOT_ALLOWED",
        "허용되지 않은 HTTP 메서드입니다.",
        allowed.isBlank() ? List.of() : List.of("허용되는 메서드: " + allowed));
  }

  @ExceptionHandler({DuplicateResourceException.class, DataIntegrityViolationException.class,
      IllegalStateException.class})
  @ResponseStatus(HttpStatus.CONFLICT)
  public ApiError handleConflict(Exception e, HttpServletRequest req) {
    String msg = (e instanceof DuplicateResourceException && e.getMessage() != null)
        ? e.getMessage()
        : "리소스 충돌이 발생했습니다.";
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
        "예상치 못한 오류가 발생했습니다.", List.of());
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
