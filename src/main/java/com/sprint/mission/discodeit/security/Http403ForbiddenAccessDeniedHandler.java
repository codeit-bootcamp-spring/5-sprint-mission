package com.sprint.mission.discodeit.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.controller.advice.ErrorResponse;
import com.sprint.mission.discodeit.exception.ErrorCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;
import java.util.Map;

public record Http403ForbiddenAccessDeniedHandler(
    ObjectMapper objectMapper
) implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ErrorCode errorCode = ErrorCode.FORBIDDEN;
        ErrorResponse errorResponse = ErrorResponse.of(
            errorCode.name(),
            errorCode.getMessage(),
            Map.of(),
            accessDeniedException,
            errorCode.getHttpStatus()
        );
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
