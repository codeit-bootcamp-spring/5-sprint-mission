package com.sprint.mission.discodeit.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.auth.LoginRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("로그인 - 성공: 올바른 자격증명으로 로그인하고 사용자 정보 반환")
    void login_Success() throws Exception {
        // given - 사용자 생성 (비밀번호 암호화)
        String rawPassword = "password123";
        String encodedPassword = passwordEncoder.encode(rawPassword);
        User user = new User("testuser", "test@example.com", encodedPassword, null);
        userRepository.save(user);

        LoginRequest request = new LoginRequest("testuser", rawPassword);

        // when & then
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(user.getId().toString()))
            .andExpect(jsonPath("$.username").value("testuser"))
            .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    @DisplayName("로그인 - 성공: 로그인 시 UserStatus의 lastActiveAt이 업데이트됨")
    void login_UpdatesLastActiveAt() throws Exception {
        // given - 사용자 생성
        String rawPassword = "password123";
        String encodedPassword = passwordEncoder.encode(rawPassword);
        User user = new User("testuser", "test@example.com", encodedPassword, null);
        userRepository.save(user);

        Instant beforeLogin = Instant.now();
        LoginRequest request = new LoginRequest("testuser", rawPassword);

        // when
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk());

        // then - lastActiveAt이 업데이트되었는지 확인
        Optional<User> updatedUser = userRepository.findById(user.getId());
        assertThat(updatedUser).isPresent();
        assertThat(updatedUser.get().getUserStatus().getLastActiveAt())
            .isAfterOrEqualTo(beforeLogin);
    }

    @Test
    @DisplayName("로그인 - 성공: 대소문자 구분 없이 username으로 로그인")
    void login_CaseInsensitive_Success() throws Exception {
        // given - username이 소문자인 사용자 생성
        String rawPassword = "password123";
        String encodedPassword = passwordEncoder.encode(rawPassword);
        User user = new User("testuser", "test@example.com", encodedPassword, null);
        userRepository.save(user);

        // 대문자로 로그인 시도
        LoginRequest request = new LoginRequest("TESTUSER", rawPassword);

        // when & then
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    @DisplayName("로그인 - 실패: 존재하지 않는 사용자로 로그인 시도")
    void login_UserNotFound_Fails() throws Exception {
        // given
        LoginRequest request = new LoginRequest("nonexistent", "password");

        // when & then
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.code").value("INVALID_CREDENTIALS"));
    }

    @Test
    @DisplayName("로그인 - 실패: 잘못된 비밀번호로 로그인 시도")
    void login_InvalidPassword_Fails() throws Exception {
        // given - 사용자 생성
        String rawPassword = "password123";
        String encodedPassword = passwordEncoder.encode(rawPassword);
        User user = new User("testuser", "test@example.com", encodedPassword, null);
        userRepository.save(user);

        // 잘못된 비밀번호로 로그인 시도
        LoginRequest request = new LoginRequest("testuser", "wrongpassword");

        // when & then
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.code").value("INVALID_CREDENTIALS"));
    }

    @Test
    @DisplayName("로그인 - 실패: 빈 username으로 로그인 시도 (유효성 검증 실패)")
    void login_EmptyUsername_Fails() throws Exception {
        // given
        LoginRequest request = new LoginRequest("", "password");

        // when & then
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("로그인 - 실패: 빈 password로 로그인 시도 (유효성 검증 실패)")
    void login_EmptyPassword_Fails() throws Exception {
        // given
        LoginRequest request = new LoginRequest("testuser", "");

        // when & then
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("로그인 - 성공: 이메일이 아닌 username으로만 로그인 가능")
    void login_WithUsername_NotEmail() throws Exception {
        // given - username과 email이 다른 사용자 생성
        String rawPassword = "password123";
        String encodedPassword = passwordEncoder.encode(rawPassword);
        User user = new User("johndoe", "john@example.com", encodedPassword, null);
        userRepository.save(user);

        // username으로 로그인 성공
        LoginRequest requestWithUsername = new LoginRequest("johndoe", rawPassword);

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestWithUsername)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username").value("johndoe"));

        // email로 로그인 시도하면 실패
        LoginRequest requestWithEmail = new LoginRequest("john@example.com", rawPassword);

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestWithEmail)))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.code").value("INVALID_CREDENTIALS"));
    }

    @Test
    @DisplayName("로그인 - 성공: 여러 번 로그인해도 lastActiveAt이 계속 업데이트됨")
    void login_MultipleLogins_UpdatesLastActiveAt() throws Exception {
        // given - 사용자 생성
        String rawPassword = "password123";
        String encodedPassword = passwordEncoder.encode(rawPassword);
        User user = new User("testuser", "test@example.com", encodedPassword, null);
        userRepository.save(user);

        LoginRequest request = new LoginRequest("testuser", rawPassword);

        // 첫 번째 로그인
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk());

        Optional<User> afterFirstLogin = userRepository.findById(user.getId());
        if (afterFirstLogin.isEmpty()) {
            throw new IllegalStateException("User should exist after first login");
        }
        Instant firstLoginTime = afterFirstLogin.get().getUserStatus().getLastActiveAt();

        // 약간의 시간 경과 후 두 번째 로그인
        Thread.sleep(100);

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk());

        // then - lastActiveAt이 더 최신 시간으로 업데이트됨
        Optional<User> afterSecondLogin = userRepository.findById(user.getId());
        if (afterSecondLogin.isEmpty()) {
            throw new IllegalStateException("User should exist after second login");
        }
        Instant secondLoginTime = afterSecondLogin.get().getUserStatus().getLastActiveAt();

        assertThat(secondLoginTime).isAfter(firstLoginTime);
    }
}
