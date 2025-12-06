package com.sprint.mission.discodeit.global.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.common.domain.exception.DiscodeitException;
import com.sprint.mission.discodeit.global.error.ErrorResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class JwtResponseWriter {

    private final ObjectMapper objectMapper;

    public void writeSuccess(HttpServletResponse response, Object body) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }

    public void writeError(HttpServletResponse response, DiscodeitException exception) throws IOException {
        ErrorResponse errorResponse = ErrorResponse.from(exception);

        response.setStatus(exception.getErrorCode().getHttpStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
