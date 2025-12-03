package com.sprint.mission.discodeit.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.exception.base.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;

import static jakarta.servlet.http.HttpServletResponse.*;
import static org.springframework.http.MediaType.*;

@Component
@RequiredArgsConstructor
public class Http403ForbiddenAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException
    ) throws IOException {

        ErrorResponse error = new ErrorResponse(
                Instant.now(),
                "ACCESS_DENIED",
                "해당 요청에 대한 권한이 없습니다.",
                Map.of("reason", accessDeniedException.getMessage()),
                accessDeniedException.getClass().getSimpleName(),
                SC_FORBIDDEN
        );

        response.setStatus(SC_FORBIDDEN);
        response.setContentType(APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(error));
    }
}
