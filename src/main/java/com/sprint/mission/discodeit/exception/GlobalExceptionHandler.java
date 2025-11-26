package com.sprint.mission.discodeit.exception;

import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.exception.binarycontent.FileSizeException;
import com.sprint.mission.discodeit.exception.binarycontent.FileIOErrorException;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.DuplicateChannelNameException;
import com.sprint.mission.discodeit.exception.channel.NotChannelMemberException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelUpdateException;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.message.UnauthorizedMessageAccessException;
import com.sprint.mission.discodeit.exception.readstatus.AlreadyExistsReadStatusException;
import com.sprint.mission.discodeit.exception.readstatus.ReadStatusNotFoundException;
import com.sprint.mission.discodeit.exception.user.DuplicateUserException;
import com.sprint.mission.discodeit.exception.user.InvalidCredentialsException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.exception.userstatus.AlreadyExistsUserStatusException;
import com.sprint.mission.discodeit.exception.userstatus.UserStatusNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({
            UserNotFoundException.class,
            ChannelNotFoundException.class,
            MessageNotFoundException.class,
            BinaryContentNotFoundException.class,
            UserStatusNotFoundException.class,
            ReadStatusNotFoundException.class,
            NoResourceFoundException.class
    })
    public ResponseEntity<ErrorResponse> handleNotFound(DiscodeitException e) {
        log.warn("[Exception] 리소스를 찾을 수 없음: {}", e.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(e, HttpStatus.NOT_FOUND.value());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler({
            DuplicateUserException.class,
            DuplicateChannelNameException.class,
            AlreadyExistsUserStatusException.class,
            AlreadyExistsReadStatusException.class
    })
    public ResponseEntity<ErrorResponse> handleConflict(DiscodeitException e) {
        log.warn("[Exception] 리소스 충돌: {}", e.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(e, HttpStatus.CONFLICT.value());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    @ExceptionHandler({
            InvalidCredentialsException.class,
            UnauthorizedMessageAccessException.class
    })
    public ResponseEntity<ErrorResponse> handleUnauthorized(DiscodeitException e) {
        log.warn("[Exception] 인증 실패: {}", e.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(e, HttpStatus.UNAUTHORIZED.value());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    @ExceptionHandler({
            NotChannelMemberException.class,
            PrivateChannelUpdateException.class
    })
    public ResponseEntity<ErrorResponse> handleForbidden(DiscodeitException e) {
        log.warn("[Exception] 권한 없음: {}", e.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(e, HttpStatus.FORBIDDEN.value());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }

    @ExceptionHandler({
            FileSizeException.class
    })
    public ResponseEntity<ErrorResponse> handleBadRequest(DiscodeitException e) {
        log.warn("[Exception] 잘못된 요청: {}", e.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(e, HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(FileIOErrorException.class)
    public ResponseEntity<ErrorResponse> handleFileUploadError(DiscodeitException e) {
        log.error("[Exception] 파일 업로드 오류: {}", e.getMessage(), e);
        ErrorResponse errorResponse = new ErrorResponse(e, HttpStatus.INTERNAL_SERVER_ERROR.value());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ErrorResponse> handleDatabaseError(DataAccessException e) {
        log.error("[Exception] 데이터베이스 오류: {}", e.getMessage(), e);
        ErrorResponse errorResponse = new ErrorResponse(e, HttpStatus.INTERNAL_SERVER_ERROR.value());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception e) {
        log.error("[Exception] 예상치 못한 오류: {}", e.getMessage(), e);
        ErrorResponse errorResponse = new ErrorResponse(e, HttpStatus.INTERNAL_SERVER_ERROR.value());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        log.error("요청 유효성 검사 실패: {}", ex.getMessage());

        Map<String, Object> validationErrors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            validationErrors.put(fieldName, errorMessage);
        });

        ErrorResponse response = new ErrorResponse(
                Instant.now(),
                "VALIDATION_ERROR",
                "요청 데이터 유효성 검사에 실패했습니다",
                validationErrors,
                ex.getClass().getSimpleName(),
                HttpStatus.BAD_REQUEST.value()
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }
}