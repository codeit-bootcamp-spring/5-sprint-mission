package com.sprint.mission.discodeit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.exception.GlobalExceptionHandler;
import com.sprint.mission.discodeit.exception.InvalidCredentialsException;
import com.sprint.mission.discodeit.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@Import(GlobalExceptionHandler.class)
@ActiveProfiles("test")
class AuthControllerTest {

  @Autowired MockMvc mvc;
  @Autowired ObjectMapper om;

  @SuppressWarnings({"removal", "deprecation"})
  @org.springframework.boot.test.mock.mockito.MockBean(AuthService.class)
  AuthService authService;

  @Test
  void login_success() throws Exception {
    var req = new LoginRequest("neo", "secret123");
    var dto = new UserDto(UUID.randomUUID(), "neo", "neo@matrix.io", null, true);

    given(authService.login(eq(req))).willReturn(dto);

    mvc.perform(post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(om.writeValueAsString(req)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.username").value("neo"));
  }

  @Test
  void login_fail_invalidCredentials() throws Exception {
    var req = new LoginRequest("neo", "wrong");

    given(authService.login(eq(req)))
        .willThrow(new InvalidCredentialsException("neo"));

    mvc.perform(post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(om.writeValueAsString(req)))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
  }
}
