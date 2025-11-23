package com.sprint.mission.discodeit.integration;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.service.UserService;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AuthApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    @Test
    @DisplayName("로그인 API 통합 테스트 - 성공")
    void login_Success() throws Exception {
        // Given: 테스트 사용자 생성 (비밀번호는 BCrypt로 저장됨)
        UserCreateRequest userRequest = new UserCreateRequest(
            "loginuser",
            "login@example.com",
            "Password1!"
        );
        userService.create(userRequest, Optional.empty());

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                .with(csrf()) // CSRF 보호 활성화 상태이므로 추가
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("username", "loginuser")
                .param("password", "Password1!")
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", notNullValue()))
            .andExpect(jsonPath("$.username", is("loginuser")))
            .andExpect(jsonPath("$.email", is("login@example.com")));
    }

    @Test
    @DisplayName("로그인 API 통합 테스트 - 실패 (존재하지 않는 사용자)")
    void login_Failure_UserNotFound() throws Exception {
        // Given: 존재하지 않는 사용자로 로그인 시도

        mockMvc.perform(post("/api/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("username", "nonexistentuser")
                .param("password", "Password1!")
            )
            // UserDetailsService 에서 UsernameNotFoundException → FailureHandler → 401
            .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("로그인 API 통합 테스트 - 실패 (잘못된 비밀번호)")
    void login_Failure_InvalidCredentials() throws Exception {
        // Given: 정상 사용자 생성
        UserCreateRequest userRequest = new UserCreateRequest(
            "loginuser2",
            "login2@example.com",
            "Password1!"
        );
        userService.create(userRequest, Optional.empty());

        // When & Then: 틀린 비밀번호로 로그인 시도
        mockMvc.perform(post("/api/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("username", "loginuser2")
                .param("password", "WrongPassword1!")
            )
            .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("로그인 API 통합 테스트 - 실패 (유효하지 않은 요청)")
    void login_Failure_InvalidRequest() throws Exception {
        // Given: username, password 둘 다 비어있는 요청
        mockMvc.perform(post("/api/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("username", "")
                .param("password", "")
            )
            // 파라미터가 비어 있어도 AuthenticationException → FailureHandler → 401
            .andExpect(status().isUnauthorized());
    }
}