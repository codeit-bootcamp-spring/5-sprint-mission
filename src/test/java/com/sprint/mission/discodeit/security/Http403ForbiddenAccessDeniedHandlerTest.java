package com.sprint.mission.discodeit.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
@DisplayName("Http403ForbiddenAccessDeniedHandler 단위 테스트")
class Http403ForbiddenAccessDeniedHandlerTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    private Http403ForbiddenAccessDeniedHandler handler;
    private StringWriter responseWriter;

    @BeforeEach
    void setUp() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        handler = new Http403ForbiddenAccessDeniedHandler(objectMapper);
        responseWriter = new StringWriter();
        given(response.getWriter()).willReturn(new PrintWriter(responseWriter));
    }

    @Test
    @DisplayName("handle - 403 상태 코드를 설정한다")
    void handle_SetsForbiddenStatus() throws Exception {
        // given
        AccessDeniedException exception = new AccessDeniedException("Access denied");

        // when
        handler.handle(request, response, exception);

        // then
        then(response).should().setStatus(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @DisplayName("handle - JSON Content-Type을 설정한다")
    void handle_SetsJsonContentType() throws Exception {
        // given
        AccessDeniedException exception = new AccessDeniedException("Access denied");

        // when
        handler.handle(request, response, exception);

        // then
        then(response).should().setContentType(MediaType.APPLICATION_JSON_VALUE);
    }

    @Test
    @DisplayName("handle - UTF-8 인코딩을 설정한다")
    void handle_SetsUtf8Encoding() throws Exception {
        // given
        AccessDeniedException exception = new AccessDeniedException("Access denied");

        // when
        handler.handle(request, response, exception);

        // then
        then(response).should().setCharacterEncoding("UTF-8");
    }

    @Test
    @DisplayName("handle - ErrorResponse JSON을 응답에 작성한다")
    void handle_WritesErrorResponse() throws Exception {
        // given
        AccessDeniedException exception = new AccessDeniedException("Access denied");

        // when
        handler.handle(request, response, exception);

        // then
        String responseBody = responseWriter.toString();
        assertThat(responseBody).contains("INSUFFICIENT_ROLE");
    }

    @Test
    @DisplayName("handle - 응답 본문에 exceptionType이 포함된다")
    void handle_ResponseContainsExceptionType() throws Exception {
        // given
        AccessDeniedException exception = new AccessDeniedException("Access denied");

        // when
        handler.handle(request, response, exception);

        // then
        String responseBody = responseWriter.toString();
        assertThat(responseBody).contains("exceptionType");
        assertThat(responseBody).contains("InsufficientRoleException");
    }
}
