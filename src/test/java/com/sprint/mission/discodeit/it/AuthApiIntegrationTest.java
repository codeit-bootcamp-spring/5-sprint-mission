package com.sprint.mission.discodeit.it;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AuthApiIntegrationTest extends BaseIntegrationTest {

  @Autowired MockMvc mvc;
  @Autowired ObjectMapper om;
  @Autowired UserRepository userRepository;

  @BeforeEach
  void setUpUser() {
    userRepository.save(new User("neo", "neo@matrix.io", "secret123", null));
  }

  @Test
  void login_success() throws Exception {
    var req = new LoginRequest("neo", "secret123");

    mvc.perform(post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(om.writeValueAsString(req)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.username").value("neo"))
        .andExpect(jsonPath("$.email").value("neo@matrix.io"));
  }

  @Test
  void login_fail_wrongPassword() throws Exception {
    var req = new LoginRequest("neo", "wrong");

    mvc.perform(post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(om.writeValueAsString(req)))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
  }
}
