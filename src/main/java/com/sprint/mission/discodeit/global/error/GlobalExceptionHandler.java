package com.sprint.mission.discodeit.global.error;

import com.sprint.mission.discodeit.global.api.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.HttpRequestMethodNotSupportedException;

import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiResponse<Void>> handleApi(ApiException e) {
        var code = e.getErrorCode();
        return ResponseEntity.status(code.status())
                .body(ApiResponse.fail(new ApiResponse.ApiError(code.name(), e.getMessage())));
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public ResponseEntity<ApiResponse<Void>> handleValidation(Exception e) {
        String msg = extractValidationMessage(e);
        return ResponseEntity.status(ErrorCode.VALIDATION_ERROR.status())
                .body(ApiResponse.fail(new ApiResponse.ApiError(ErrorCode.VALIDATION_ERROR.name(), msg)));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<Void>> handleMissingParam(MissingServletRequestParameterException e) {
        String msg = "필수 파라미터 누락: " + e.getParameterName();
        return ResponseEntity.status(ErrorCode.BAD_REQUEST.status())
                .body(ApiResponse.fail(new ApiResponse.ApiError(ErrorCode.BAD_REQUEST.name(), msg)));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodNotAllowed(HttpRequestMethodNotSupportedException e) {
        String msg = "허용되지 않은 메서드입니다. method=" + e.getMethod();
        return ResponseEntity.status(ErrorCode.BAD_REQUEST.status())
                .body(ApiResponse.fail(new ApiResponse.ApiError(ErrorCode.BAD_REQUEST.name(), msg)));
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ApiResponse<Void>> handleNoSuchElement(NoSuchElementException e) {
        return ResponseEntity.status(ErrorCode.NOT_FOUND.status())
                .body(ApiResponse.fail(new ApiResponse.ApiError(ErrorCode.NOT_FOUND.name(), ErrorCode.NOT_FOUND.defaultMessage())));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleEtc(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.fail(new ApiResponse.ApiError(ErrorCode.INTERNAL_ERROR.name(), ErrorCode.INTERNAL_ERROR.defaultMessage())));
    }

    private String extractValidationMessage(Exception e) {
        if (e instanceof MethodArgumentNotValidException manv && manv.getBindingResult().hasErrors()) {
            var fe = manv.getBindingResult().getFieldErrors().get(0);
            return fe.getField() + ": " + fe.getDefaultMessage();
        }
        if (e instanceof BindException be && be.getBindingResult().hasErrors()) {
            var fe = be.getBindingResult().getFieldErrors().get(0);
            return fe.getField() + ": " + fe.getDefaultMessage();
        }
        return ErrorCode.VALIDATION_ERROR.defaultMessage();
    }
}
