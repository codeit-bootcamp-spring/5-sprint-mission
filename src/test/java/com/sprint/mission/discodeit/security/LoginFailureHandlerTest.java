package com.sprint.mission.discodeit.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sprint.mission.discodeit.domain.service.AuthMetricsService;
import com.sprint.mission.discodeit.global.security.LoginFailureHandler;
import com.sprint.mission.discodeit.infra.event.audit.AuthAuditPublisher;
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
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
@DisplayName("LoginFailureHandler 단위 테스트")
class LoginFailureHandlerTest {

    @Mock
    private AuthAuditPublisher authAuditPublisher;

    @Mock
    private AuthMetricsService authMetricsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    private ObjectMapper objectMapper;
    private LoginFailureHandler loginFailureHandler;
    private StringWriter responseWriter;

    @BeforeEach
    void setUp() throws Exception {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        loginFailureHandler = new LoginFailureHandler(objectMapper, authAuditPublisher, authMetricsService);
        responseWriter = new StringWriter();
        given(response.getWriter()).willReturn(new PrintWriter(responseWriter));
    }

    @Test
    @DisplayName("onAuthenticationFailure - 401 상태 코드를 설정한다")
    void onAuthenticationFailure_SetsUnauthorizedStatus() throws Exception {
        // given
        String username = "testuser";
        AuthenticationException exception = new BadCredentialsException("Bad credentials");
        given(request.getParameter("username")).willReturn(username);

        // when
        loginFailureHandler.onAuthenticationFailure(request, response, exception);

        // then
        then(response).should().setStatus(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    @DisplayName("onAuthenticationFailure - JSON Content-Type을 설정한다")
    void onAuthenticationFailure_SetsJsonContentType() throws Exception {
        // given
        AuthenticationException exception = new BadCredentialsException("Bad credentials");
        given(request.getParameter("username")).willReturn("testuser");

        // when
        loginFailureHandler.onAuthenticationFailure(request, response, exception);

        // then
        then(response).should().setContentType(MediaType.APPLICATION_JSON_VALUE);
    }

    @Test
    @DisplayName("onAuthenticationFailure - UTF-8 인코딩을 설정한다")
    void onAuthenticationFailure_SetsUtf8Encoding() throws Exception {
        // given
        AuthenticationException exception = new BadCredentialsException("Bad credentials");
        given(request.getParameter("username")).willReturn("testuser");

        // when
        loginFailureHandler.onAuthenticationFailure(request, response, exception);

        // then
        then(response).should().setCharacterEncoding("UTF-8");
    }

    @Test
    @DisplayName("onAuthenticationFailure - 감사 로그를 기록한다")
    void onAuthenticationFailure_LogsAudit() throws Exception {
        // given
        String username = "testuser";
        String errorMessage = "Bad credentials";
        AuthenticationException exception = new BadCredentialsException(errorMessage);
        given(request.getParameter("username")).willReturn(username);

        // when
        loginFailureHandler.onAuthenticationFailure(request, response, exception);

        // then
        then(authAuditPublisher).should().logLoginFailure(username, request, errorMessage);
    }

    @Test
    @DisplayName("onAuthenticationFailure - 메트릭을 기록한다")
    void onAuthenticationFailure_RecordsMetrics() throws Exception {
        // given
        AuthenticationException exception = new BadCredentialsException("Bad credentials");
        given(request.getParameter("username")).willReturn("testuser");

        // when
        loginFailureHandler.onAuthenticationFailure(request, response, exception);

        // then
        then(authMetricsService).should().recordLoginAttempt(false);
    }

    @Test
    @DisplayName("onAuthenticationFailure - ErrorResponse JSON을 응답에 작성한다")
    void onAuthenticationFailure_WritesErrorResponse() throws Exception {
        // given
        AuthenticationException exception = new BadCredentialsException("Bad credentials");
        given(request.getParameter("username")).willReturn("testuser");

        // when
        loginFailureHandler.onAuthenticationFailure(request, response, exception);

        // then
        String responseBody = responseWriter.toString();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        assertThat(jsonNode.has("code")).isTrue();
        assertThat(jsonNode.get("code").asText()).isEqualTo("INVALID_CREDENTIALS");
    }
}
