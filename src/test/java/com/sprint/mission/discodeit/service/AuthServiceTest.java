package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.basic.BasicAuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class AuthServiceTest {

  @Mock
  private UserRepository userRepository;
  @Mock
  private UserMapper userMapper;

  @InjectMocks
  private BasicAuthService authService;

  @Test
  void login_success() {
    var req = new LoginRequest("neo", "secret123");

    var user = mock(User.class);
    given(userRepository.findByUsername("neo")).willReturn(Optional.of(user));
    given(user.getPassword()).willReturn("secret123");

    var userId = UUID.randomUUID();
    var dto = new UserDto(userId, "neo", "neo@matrix.io", null, true);
    given(userMapper.toDto(user)).willReturn(dto);

    var result = authService.login(req);

    assertThat(result).isEqualTo(dto);
    then(userRepository).should().findByUsername("neo");
    then(userMapper).should().toDto(user);
  }

  @Test
  void login_fail_userNotFound() {
    var req = new LoginRequest("unknown", "pw");
    given(userRepository.findByUsername("unknown")).willReturn(Optional.empty());

    assertThatThrownBy(() -> authService.login(req)).isInstanceOf(NoSuchElementException.class);
  }

  @Test
  void login_fail_wrongPassword() {
    var req = new LoginRequest("neo", "wrong");
    var user = mock(User.class);
    given(userRepository.findByUsername("neo")).willReturn(Optional.of(user));
    given(user.getPassword()).willReturn("secret123");

    assertThatThrownBy(() -> authService.login(req)).isInstanceOf(IllegalArgumentException.class);
  }
}
