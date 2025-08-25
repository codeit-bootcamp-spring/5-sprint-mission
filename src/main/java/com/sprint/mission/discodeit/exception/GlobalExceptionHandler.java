package com.sprint.mission.discodeit.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private ResponseEntity<String> plain(HttpStatus status, String message) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "text/plain;charset=UTF-8");
        return new ResponseEntity<>(StringUtils.hasText(message) ? message : status.getReasonPhrase(), headers, status);
    }

    // 404 NOT_FOUND : 리소스 없음
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<String> handleNotFound(NoSuchElementException e) {
        return plain(HttpStatus.NOT_FOUND, e.getMessage());
    }

    // 400 BAD_REQUEST : 비즈니스 검증 오류/중복
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleBadRequest(IllegalArgumentException e) {
        return plain(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    // 400 BAD_REQUEST : 필수 파라미터 누락 userId 등..
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<String> handleMissingParam(MissingServletRequestParameterException e) {
        String msg = "Missing required parameter: " + e.getParameterName();
        return plain(HttpStatus.BAD_REQUEST, msg);
    }

    // 400 BAD_REQUEST : 경로/쿼리 타입 불일치
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<String> handleTypeMismatch(MethodArgumentTypeMismatchException  e) {
        String name = e.getName();
        String required = e.getRequiredType() != null ? e.getRequiredType().getSimpleName() : "required Type";
        String msg = "Parameter '" + name + "' has invalid format (expected " + required + ")";
        return plain(HttpStatus.BAD_REQUEST, msg);
    }

    // 400 Bad Request : JSON 바디 누락/파싱 오류
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> handleNotReadable(HttpMessageNotReadableException e) {
        return plain(HttpStatus.BAD_REQUEST, "Malformed JSON request body");
    }

    // 400 Bad Request: @Valid 검증 실패
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidation(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(err -> "Validation failed: " + err.getField() + " " + err.getDefaultMessage())
                .orElse("Validation failed");
        return plain(HttpStatus.BAD_REQUEST, msg);
    }

    // 400 Bad Request: 멀티파트 업로드 이슈(파일 누락/형식/사이즈)
    @ExceptionHandler({MultipartException.class, MaxUploadSizeExceededException.class})
    public ResponseEntity<String> handleMultipart(Exception e) {
        return plain(HttpStatus.BAD_REQUEST, "Invalid multipart request");
    }

    // 404: 잘못된 URL (옵션 — 스프링 설정에서 throw-exception-if-no-handler=true 필요)
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<String> handleNoHandler(NoHandlerFoundException e) {
        return plain(HttpStatus.NOT_FOUND, "Endpoint not found");
    }

    // 500: 기타 예기치 않은 오류
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleInternal(Exception e) {
        return plain(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");
    }
}
