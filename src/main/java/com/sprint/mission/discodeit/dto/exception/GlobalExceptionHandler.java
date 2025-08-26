package com.sprint.mission.discodeit.dto.exception;

import com.sprint.mission.discodeit.dto.api.ApiResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.io.IOException;
import java.util.NoSuchElementException;

@RestControllerAdvice // Rest 관련 에러처리 어노테이션
public class GlobalExceptionHandler {
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ApiResult<Void>> handleNotFound(NoSuchElementException e) {
        ApiResult<Void> body = ApiResult.fail("NOT_FOUND", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler({
            IllegalArgumentException.class,
            MissingServletRequestPartException.class,
            MethodArgumentTypeMismatchException.class
    })
    public ResponseEntity<ApiResult<Void>> handleBadRequest(Exception e) {
        ApiResult<Void> body = ApiResult.fail("BAD_REQUEST", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiResult<Void>> handleMaxUpload(MaxUploadSizeExceededException e) {
        ApiResult<Void> body = ApiResult.fail("PAYLOAD_TOO_LARGE", "업로드 가능한 파일 크기를 초과했습니다.");
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(body);
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<ApiResult<Void>> handleIO(IOException e) {
        ApiResult<Void> body = ApiResult.fail("IO_ERROR", "파일 처리 중 오류가 발생했습니다.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    // 모든 에러 처리
    @ExceptionHandler({Exception.class})
    public ResponseEntity<ApiResult<Void>> handleException(Exception e) {
//        ApiResult<Void> apiResult = ApiResult.fail("INTERNAL_ERROR", "서버 내부 오류가 발생했습니다.")
        ApiResult<Void> apiResult = ApiResult.fail("INTERNAL_ERROR",
                e.getMessage() != null ? e.getMessage() : "서버 내부 오류가 발생했습니다." );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResult);
    }
}

