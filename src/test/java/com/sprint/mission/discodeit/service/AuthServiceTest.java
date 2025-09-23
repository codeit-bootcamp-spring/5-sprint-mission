package com.sprint.mission.discodeit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.sprint.mission.discodeit.dto.AuthDto.Login;
import com.sprint.mission.discodeit.dto.UserDto.Detail;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.auth.InvalidUserStatusException;
import com.sprint.mission.discodeit.exception.auth.LoginFailedException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.basic.BasicAuthService;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private UserStatusRepository userStatusRepository;

  @Mock
  private UserMapper userMapper;

  @InjectMocks
  private BasicAuthService authService;

  private User user;
  private UserStatus userStatus;
  private Login loginRequest;
  private Detail detail;

  @BeforeEach
  void setUp() {

    user = User.builder()
               .username("testuser")
               .password("password")
               .build();

    userStatus = UserStatus.builder()
                           .user(user)
                           .lastActiveAt(Instant.now())
                           .build();

    loginRequest = Login.builder()
                        .username("testuser")
                        .password("password")
                        .build();

    detail = Detail.builder()
                   .id(user.getId())
                   .username(user.getUsername())
                   .email("test@example.com")
                   .online(true)
                   .build();
  }

  @Test
  void login_success() {

    given(userRepository.findByUsername(loginRequest.getUsername())).willReturn(Optional.of(user));
    given(userStatusRepository.findByUserId(user.getId())).willReturn(Optional.of(userStatus));
    given(userMapper.toDetail(user)).willReturn(detail);

    Detail result = authService.login(loginRequest);

    assertThat(result).isNotNull();
    assertThat(result.getUsername()).isEqualTo("testuser");
    then(userStatusRepository).should()
                              .save(userStatus);
  }

  @Test
  void login_fail_userNotFound() {
    given(userRepository.findByUsername(loginRequest.getUsername())).willReturn(Optional.empty());

    assertThrows(LoginFailedException.class, () -> authService.login(loginRequest));
  }

  @Test
  void login_fail_invalidPassword() {

    given(userRepository.findByUsername(loginRequest.getUsername())).willReturn(Optional.of(user));

    Login wrongLogin = Login.builder()
                            .username("testuser")
                            .password("wrongPass")
                            .build();

    assertThrows(LoginFailedException.class, () -> authService.login(wrongLogin));
  }


  @Test
  void login_fail_userStatusNotFound() {
    given(userRepository.findByUsername(loginRequest.getUsername())).willReturn(Optional.of(user));
    given(userStatusRepository.findByUserId(user.getId())).willReturn(Optional.empty());

    assertThrows(InvalidUserStatusException.class, () -> authService.login(loginRequest));
  }
}
