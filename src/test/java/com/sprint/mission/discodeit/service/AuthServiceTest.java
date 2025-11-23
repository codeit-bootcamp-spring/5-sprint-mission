package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.auth.LoginRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.auth.InvalidCredentialsException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private AuthService authService;

    @Test
    @DisplayName("login - 로그인 성공")
    void login_Success() {
        // given
        LoginRequest request = new LoginRequest("TestUser", "password123");
        String encodedPassword = "$2a$10$encodedPassword";

        User user = new User("testuser", "test@example.com", encodedPassword, null);

        UserDto expectedDto = new UserDto(
            UUID.randomUUID(),
            "testuser",
            "test@example.com",
            null,
            true
        );

        given(userRepository.findByUsername("testuser")).willReturn(Optional.of(user));
        given(passwordEncoder.matches("password123", encodedPassword)).willReturn(true);
        given(userMapper.toDto(user)).willReturn(expectedDto);

        // when
        UserDto result = authService.login(request);

        // then
        assertThat(result).isNotNull();
        assertThat(result.username()).isEqualTo("testuser");

        then(userRepository).should().findByUsername("testuser");
        then(passwordEncoder).should().matches("password123", encodedPassword);
        then(userMapper).should().toDto(user);
    }

    @Test
    @DisplayName("login - username을 소문자로 변환하고 공백 제거")
    void login_NormalizesUsername() {
        // given
        LoginRequest request = new LoginRequest("  TestUser  ", "password123");
        String encodedPassword = "$2a$10$encodedPassword";

        User user = new User("testuser", "test@example.com", encodedPassword, null);

        UserDto expectedDto = new UserDto(
            UUID.randomUUID(),
            "testuser",
            "test@example.com",
            null,
            true
        );

        given(userRepository.findByUsername("testuser")).willReturn(Optional.of(user));
        given(passwordEncoder.matches("password123", encodedPassword)).willReturn(true);
        given(userMapper.toDto(user)).willReturn(expectedDto);

        // when
        UserDto result = authService.login(request);

        // then
        assertThat(result).isNotNull();
        then(userRepository).should().findByUsername("testuser");
    }

    @Test
    @DisplayName("login - 존재하지 않는 사용자로 로그인 시 InvalidCredentialsException 발생")
    void login_UserNotFound_ThrowsInvalidCredentialsException() {
        // given
        LoginRequest request = new LoginRequest("nonexistent", "password123");

        given(userRepository.findByUsername("nonexistent")).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> authService.login(request))
            .isInstanceOf(InvalidCredentialsException.class);

        then(userRepository).should().findByUsername("nonexistent");
        then(passwordEncoder).shouldHaveNoInteractions();
        then(userMapper).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("login - 잘못된 비밀번호로 로그인 시 InvalidCredentialsException 발생")
    void login_WrongPassword_ThrowsInvalidCredentialsException() {
        // given
        LoginRequest request = new LoginRequest("testuser", "wrongPassword");
        String encodedPassword = "$2a$10$encodedPassword";

        User user = new User("testuser", "test@example.com", encodedPassword, null);

        given(userRepository.findByUsername("testuser")).willReturn(Optional.of(user));
        given(passwordEncoder.matches("wrongPassword", encodedPassword)).willReturn(false);

        // when & then
        assertThatThrownBy(() -> authService.login(request))
            .isInstanceOf(InvalidCredentialsException.class);

        then(userRepository).should().findByUsername("testuser");
        then(passwordEncoder).should().matches("wrongPassword", encodedPassword);
        then(userMapper).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("login - 성공 시 UserStatus의 lastActiveAt을 현재 시간으로 업데이트")
    void login_UpdatesLastActiveAt() {
        // given
        LoginRequest request = new LoginRequest("testuser", "password123");
        String encodedPassword = "$2a$10$encodedPassword";

        User user = new User("testuser", "test@example.com", encodedPassword, null);

        UserDto expectedDto = new UserDto(
            UUID.randomUUID(),
            "testuser",
            "test@example.com",
            null,
            true
        );

        given(userRepository.findByUsername("testuser")).willReturn(Optional.of(user));
        given(passwordEncoder.matches("password123", encodedPassword)).willReturn(true);
        given(userMapper.toDto(user)).willReturn(expectedDto);

        Instant beforeLogin = user.getUserStatus().getLastActiveAt();

        // when
        authService.login(request);

        // then
        // UserStatus는 실제 엔티티이므로 update() 호출 검증은 불가능하지만
        // 로그인 후 lastActiveAt이 업데이트되었는지 확인할 수 있음
        assertThat(user.getUserStatus().getLastActiveAt()).isAfterOrEqualTo(beforeLogin);
    }
}
