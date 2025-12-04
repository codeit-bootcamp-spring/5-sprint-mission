package com.sprint.mission.discodeit.global.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.domain.service.AuthMetricsService;
import com.sprint.mission.discodeit.global.exception.ErrorResponse;
import com.sprint.mission.discodeit.global.exception.auth.InvalidCredentialsException;
import com.sprint.mission.discodeit.infra.event.audit.AuthAuditPublisher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoginFailureHandler implements AuthenticationFailureHandler {

    private final ObjectMapper objectMapper;
    private final AuthAuditPublisher authAuditPublisher;
    private final AuthMetricsService authMetricsService;

    @Override
    public void onAuthenticationFailure(
        HttpServletRequest request,
        HttpServletResponse response,
        AuthenticationException exception
    ) throws IOException, ServletException {
        String username = request.getParameter("username");
        log.debug("로그인 실패: username={}, reason={}", username, exception.getMessage());

        authAuditPublisher.logLoginFailure(username, request, exception.getMessage());
        authMetricsService.recordLoginAttempt(false);
        // 로그인/로그아웃 users, userId 캐시 evict

        InvalidCredentialsException discodeitException = new InvalidCredentialsException();
        ErrorResponse errorResponse = ErrorResponse.from(discodeitException);
        response.setStatus(discodeitException.getErrorCode().getHttpStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
