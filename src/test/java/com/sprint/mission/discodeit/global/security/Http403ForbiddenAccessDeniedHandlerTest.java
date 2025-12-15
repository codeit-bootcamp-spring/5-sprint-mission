package com.sprint.mission.discodeit.global.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.common.domain.exception.ErrorCode;
import com.sprint.mission.discodeit.global.error.ErrorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.access.AccessDeniedException;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Http403ForbiddenAccessDeniedHandler 단위 테스트")
class Http403ForbiddenAccessDeniedHandlerTest {

    private Http403ForbiddenAccessDeniedHandler handler;
    private ObjectMapper objectMapper;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        handler = new Http403ForbiddenAccessDeniedHandler(objectMapper);
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }

    @Test
    @DisplayName("AccessDeniedException 발생 시 403 상태와 InsufficientRoleException ErrorResponse 반환")
    void handle_returnsInsufficientRoleErrorResponse() throws Exception {
        // given
        request.setRequestURI("/api/channels/public");
        request.setMethod("POST");
        AccessDeniedException accessDeniedException = new AccessDeniedException("Access Denied");

        // when
        handler.handle(request, response, accessDeniedException);

        // then
        assertThat(response.getStatus()).isEqualTo(403);
        assertThat(response.getContentType()).startsWith(MediaType.APPLICATION_JSON_VALUE);
        assertThat(response.getCharacterEncoding()).isEqualTo(StandardCharsets.UTF_8.name());

        ErrorResponse errorResponse = objectMapper.readValue(
            response.getContentAsString(), ErrorResponse.class
        );
        assertThat(errorResponse.code()).isEqualTo(ErrorCode.INSUFFICIENT_ROLE.name());
        assertThat(errorResponse.message()).isEqualTo(ErrorCode.INSUFFICIENT_ROLE.getMessage());
        assertThat(errorResponse.exceptionType()).isEqualTo("InsufficientRoleException");
        assertThat(errorResponse.status()).isEqualTo(403);
    }

    @Test
    @DisplayName("다른 AccessDeniedException 메시지도 동일하게 InsufficientRoleException으로 변환")
    void handle_withDifferentMessage_returnsInsufficientRoleErrorResponse() throws Exception {
        // given
        request.setRequestURI("/api/auth/role");
        request.setMethod("PUT");
        AccessDeniedException accessDeniedException = new AccessDeniedException(
            "Access is denied due to insufficient permissions"
        );

        // when
        handler.handle(request, response, accessDeniedException);

        // then
        assertThat(response.getStatus()).isEqualTo(403);

        ErrorResponse errorResponse = objectMapper.readValue(
            response.getContentAsString(), ErrorResponse.class
        );
        assertThat(errorResponse.code()).isEqualTo(ErrorCode.INSUFFICIENT_ROLE.name());
        assertThat(errorResponse.exceptionType()).isEqualTo("InsufficientRoleException");
    }
}
