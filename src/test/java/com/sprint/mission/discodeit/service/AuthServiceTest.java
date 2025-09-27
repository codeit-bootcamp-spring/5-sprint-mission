package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.auth.LoginRequest;
import com.sprint.mission.discodeit.dto.response.auth.LoginResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.InvalidCredentialsException;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.basic.BasicAuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BasicAuthService authService;

    @Test
    @DisplayName("올바른 자격증명으로 로그인 성공")
    void login_success() {
        // given
        String username = "test";
        String password = "password123";

        User user = User.builder()
                .id(UUID.randomUUID())
                .username(username)
                .password(password)
                .email("test@example.com")
                .build();

        LoginRequest request = LoginRequest.builder()
                .username(username)
                .password(password)
                .build();

        given(userRepository.findByUsername(username)).willReturn(Optional.of(user));

        // when
        LoginResponse response = authService.login(request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getUsername()).isEqualTo(username);
    }

    @Test
    @DisplayName("존재하지 않는 사용자명으로 로그인 실패")
    void login_fail_userNotFound() {
        // given
        LoginRequest request = LoginRequest.builder()
                .username("nonexist")
                .password("password123")
                .build();

        given(userRepository.findByUsername("nonexist")).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    @DisplayName("잘못된 비밀번호로 로그인 실패")
    void login_fail_wrongPassword() {
        // given
        User user = User.builder()
                .id(UUID.randomUUID())
                .username("test")
                .password("correct")
                .email("test@example.com")
                .build();

        LoginRequest request = LoginRequest.builder()
                .username("test")
                .password("wrong")
                .build();

        given(userRepository.findByUsername("test")).willReturn(Optional.of(user));

        // when
        // then
        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(InvalidCredentialsException.class);
    }
}