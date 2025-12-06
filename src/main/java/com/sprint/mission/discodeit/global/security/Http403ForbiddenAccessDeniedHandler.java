package com.sprint.mission.discodeit.global.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.auth.domain.exception.InsufficientRoleException;
import com.sprint.mission.discodeit.global.error.ErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public record Http403ForbiddenAccessDeniedHandler(ObjectMapper objectMapper) implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {
        InsufficientRoleException exception = new InsufficientRoleException();
        ErrorResponse errorResponse = ErrorResponse.from(exception);
        response.setStatus(exception.getErrorCode().getHttpStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
