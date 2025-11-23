package com.sprint.mission.discodeit.controller.advice;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.user.DuplicateUsernameException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@DisplayName("GlobalExceptionHandler 단위 테스트")
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;
    private MockHttpServletRequest request;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
        request = new MockHttpServletRequest();
        request.setMethod("POST");
        request.setRequestURI("/api/users");
        request.setQueryString("page=1&size=10");
    }

    @Test
    @DisplayName("DiscodeitException 처리 - UserNotFoundException")
    void handleDiscodeitException_UserNotFoundException() {
        // given
        UserNotFoundException exception = new UserNotFoundException();

        // when
        ResponseEntity<ErrorResponse> response = handler.handleDiscodeitException(exception, request);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo(ErrorCode.USER_NOT_FOUND.name());
        assertThat(response.getBody().message()).isEqualTo(ErrorCode.USER_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("DiscodeitException 처리 - DuplicateUsernameException")
    void handleDiscodeitException_DuplicateUsernameException() {
        // given
        DuplicateUsernameException exception = new DuplicateUsernameException("testuser");

        // when
        ResponseEntity<ErrorResponse> response = handler.handleDiscodeitException(exception, request);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo(ErrorCode.DUPLICATE_USERNAME.name());
    }

    @Test
    @DisplayName("HttpMessageNotReadableException 처리 - JSON 파싱 오류")
    @SuppressWarnings("DataFlowIssue")
    void handleInvalidJson() {
        // given
        InvalidFormatException cause = mock(InvalidFormatException.class);
        given(cause.getMessage()).willReturn("Cannot deserialize value");

        HttpMessageNotReadableException exception = new HttpMessageNotReadableException(
            "JSON parse error", cause, null
        );

        // when
        ResponseEntity<ErrorResponse> response = handler.handleInvalidJson(exception, request);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo(ErrorCode.INVALID_JSON.name());
    }

    @Test
    @DisplayName("ConstraintViolationException 처리")
    void handleConstraintViolation() {
        // given
        Set<ConstraintViolation<?>> violations = new HashSet<>();
        ConstraintViolation<?> violation = mock(ConstraintViolation.class);
        Path propertyPath = mock(Path.class);

        given(propertyPath.toString()).willReturn("email");
        given(violation.getPropertyPath()).willReturn(propertyPath);
        given(violation.getMessage()).willReturn("must be a valid email");
        given(violation.getInvalidValue()).willReturn("invalid-email");

        violations.add(violation);
        ConstraintViolationException exception = new ConstraintViolationException(violations);

        // when
        ResponseEntity<ErrorResponse> response = handler.handleConstraintViolation(exception, request);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo(ErrorCode.INVALID_PARAMETER_VALUE.name());
    }

    @Test
    @DisplayName("MethodArgumentNotValidException 처리 - 필드 에러")
    @SuppressWarnings("DataFlowIssue")
    void handleValidation_WithFieldErrors() {
        // given
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "user");
        bindingResult.addError(new FieldError("user", "username", "testuser", false, null, null, "must not be blank"));
        bindingResult.addError(new FieldError("user", "email", null, false, null, null, "must be a valid email"));

        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(
            null, bindingResult
        );

        // when
        ResponseEntity<ErrorResponse> response = handler.handleValidation(exception, request);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo(ErrorCode.INVALID_BODY_VALUE.name());
    }

    @Test
    @DisplayName("MethodArgumentNotValidException 처리 - 전역 에러 포함")
    @SuppressWarnings("DataFlowIssue")
    void handleValidation_WithGlobalErrors() {
        // given
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "user");
        bindingResult.addError(new FieldError("user", "username", null, false, null, null, "must not be blank"));
        bindingResult.addError(new ObjectError("user", "passwords do not match"));

        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(
            null, bindingResult
        );

        // when
        ResponseEntity<ErrorResponse> response = handler.handleValidation(exception, request);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().details()).containsKey("globalErrors");
    }

    @Test
    @DisplayName("MethodArgumentTypeMismatchException 처리")
    @SuppressWarnings("DataFlowIssue")
    void handleParameterTypeValidation() {
        // given
        MethodArgumentTypeMismatchException exception = new MethodArgumentTypeMismatchException(
            "invalid", Long.class, "userId", null, null
        );

        // when
        ResponseEntity<ErrorResponse> response = handler.handleParameterTypeValidation(exception, request);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo(ErrorCode.INVALID_PARAMETER_VALUE.name());
    }

    @Test
    @DisplayName("MissingServletRequestParameterException 처리")
    void handleMissingParameter() {
        // given
        MissingServletRequestParameterException exception = new MissingServletRequestParameterException(
            "userId", "Long"
        );

        // when
        ResponseEntity<ErrorResponse> response = handler.handleMissingParameter(exception, request);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo(ErrorCode.MISSING_PARAMETER.name());
    }

    @Test
    @DisplayName("MissingServletRequestPartException 처리")
    void handleMissingPart() {
        // given
        MissingServletRequestPartException exception = new MissingServletRequestPartException("file");

        // when
        ResponseEntity<ErrorResponse> response = handler.handleMissingPart(exception, request);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo(ErrorCode.MISSING_PART.name());
    }

    @Test
    @DisplayName("NoHandlerFoundException 처리")
    @SuppressWarnings("DataFlowIssue")
    void handleNoHandler_NoHandlerFoundException() {
        // given
        NoHandlerFoundException exception = new NoHandlerFoundException(
            "POST", "/api/invalid", null
        );

        // when
        ResponseEntity<ErrorResponse> response = handler.handleNoHandler(exception, request);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo(ErrorCode.ENDPOINT_NOT_FOUND.name());
    }

    @Test
    @DisplayName("NoResourceFoundException 처리")
    @SuppressWarnings("DataFlowIssue")
    void handleNoHandler_NoResourceFoundException() {
        // given
        NoResourceFoundException exception = new NoResourceFoundException(null, "/api/invalid");

        // when
        ResponseEntity<ErrorResponse> response = handler.handleNoHandler(exception, request);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo(ErrorCode.ENDPOINT_NOT_FOUND.name());
    }

    @Test
    @DisplayName("HttpRequestMethodNotSupportedException 처리")
    void handleMethodNotAllowed() {
        // given
        List<String> supportedMethods = List.of("GET", "PUT");
        HttpRequestMethodNotSupportedException exception = new HttpRequestMethodNotSupportedException(
            "POST", supportedMethods
        );

        // when
        ResponseEntity<ErrorResponse> response = handler.handleMethodNotAllowed(exception, request);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.METHOD_NOT_ALLOWED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo(ErrorCode.METHOD_NOT_ALLOWED.name());
    }

    @Test
    @DisplayName("HttpMediaTypeNotAcceptableException 처리")
    void handleNotAcceptable() {
        // given
        HttpMediaTypeNotAcceptableException exception = new HttpMediaTypeNotAcceptableException(
            List.of(MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML)
        );

        // when
        ResponseEntity<ErrorResponse> response = handler.handleNotAcceptable(exception, request);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_ACCEPTABLE);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo(ErrorCode.NOT_ACCEPTABLE.name());
    }

    @Test
    @DisplayName("DataIntegrityViolationException 처리")
    void handleDataIntegrity() {
        // given
        DataIntegrityViolationException exception = new DataIntegrityViolationException(
            "Key (username)=(testuser) already exists"
        );

        // when
        ResponseEntity<ErrorResponse> response = handler.handleDataIntegrity(exception, request);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo(ErrorCode.CONFLICT.name());
    }

    @Test
    @DisplayName("MaxUploadSizeExceededException 처리")
    void handleMaxSizeExceeded() {
        // given
        MaxUploadSizeExceededException exception = new MaxUploadSizeExceededException(10_485_760);

        // when
        ResponseEntity<ErrorResponse> response = handler.handleMaxSizeExceeded(exception, request);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.PAYLOAD_TOO_LARGE);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo(ErrorCode.PAYLOAD_TOO_LARGE.name());
    }

    @Test
    @DisplayName("HttpMediaTypeNotSupportedException 처리")
    void handleMediaTypeNotSupported() {
        // given
        HttpMediaTypeNotSupportedException exception = new HttpMediaTypeNotSupportedException(
            MediaType.TEXT_PLAIN,
            List.of(MediaType.APPLICATION_JSON, MediaType.MULTIPART_FORM_DATA)
        );

        // when
        ResponseEntity<ErrorResponse> response = handler.handleMediaTypeNotSupported(exception, request);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo(ErrorCode.UNSUPPORTED_MEDIA_TYPE.name());
    }

    @Test
    @DisplayName("일반 예외 처리")
    void handleAny() {
        // given
        Exception exception = new RuntimeException("Unexpected error");

        // when
        ResponseEntity<ErrorResponse> response = handler.handleAny(exception, request);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo(ErrorCode.INTERNAL_SERVER_ERROR.name());
    }
}
