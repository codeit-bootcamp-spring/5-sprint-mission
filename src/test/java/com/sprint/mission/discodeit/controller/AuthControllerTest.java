package com.sprint.mission.discodeit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.controller.advice.GlobalExceptionHandler;
import com.sprint.mission.discodeit.dto.auth.LoginRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.exception.auth.InvalidCredentialsException;
import com.sprint.mission.discodeit.service.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import(GlobalExceptionHandler.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    @Test
    @DisplayName("POST /api/auth/login - 성공: 로그인")
    void login_Success() throws Exception {
        // given
        LoginRequest request = new LoginRequest("testuser", "password123");
        UserDto response = new UserDto(
            UUID.randomUUID(),
            "testuser",
            "test@example.com",
            null,
            true
        );

        given(authService.login(any(LoginRequest.class))).willReturn(response);

        // when & then
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username").value("testuser"))
            .andExpect(jsonPath("$.email").value("test@example.com"));

        then(authService).should().login(any(LoginRequest.class));
    }

    @Test
    @DisplayName("POST /api/auth/login - 실패: 잘못된 인증 정보")
    void login_InvalidCredentials() throws Exception {
        // given
        LoginRequest request = new LoginRequest("testuser", "wrongpassword");

        given(authService.login(any(LoginRequest.class)))
            .willThrow(new InvalidCredentialsException());

        // when & then
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.code").value("INVALID_CREDENTIALS"));

        then(authService).should().login(any(LoginRequest.class));
    }

    @Test
    @DisplayName("POST /api/auth/login - 실패: 잘못된 요청 데이터 (유효성 검증 실패)")
    void login_InvalidData() throws Exception {
        // given - username이 빈 문자열
        LoginRequest request = new LoginRequest("", "password123");

        // when & then
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }
}
