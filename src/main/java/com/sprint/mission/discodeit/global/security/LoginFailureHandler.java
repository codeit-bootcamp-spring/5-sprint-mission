package com.sprint.mission.discodeit.global.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.auth.domain.event.LoginFailureEvent;
import com.sprint.mission.discodeit.auth.domain.exception.InvalidCredentialsException;
import com.sprint.mission.discodeit.global.error.ErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static com.sprint.mission.discodeit.global.config.MdcLoggingFilter.KEY_REQUEST_START_TIME;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoginFailureHandler implements AuthenticationFailureHandler {

    private final ApplicationEventPublisher eventPublisher;

    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationFailure(
        HttpServletRequest request,
        HttpServletResponse response,
        AuthenticationException exception
    ) throws IOException, ServletException {
        String startTimeStr = MDC.get(KEY_REQUEST_START_TIME);
        long duration = -1L;
        try {
            duration = System.currentTimeMillis() - Long.parseLong(startTimeStr);
        } catch (NumberFormatException | NullPointerException e) {
            log.warn("Login audit: parsing login start time failed", e);
        }

        String username = request.getParameter("username");
        InvalidCredentialsException discodeitException = new InvalidCredentialsException(username);
        ErrorResponse errorResponse = ErrorResponse.from(discodeitException);

        response.setStatus(discodeitException.getErrorCode().getHttpStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));

        eventPublisher.publishEvent(new LoginFailureEvent(duration));
    }
}
