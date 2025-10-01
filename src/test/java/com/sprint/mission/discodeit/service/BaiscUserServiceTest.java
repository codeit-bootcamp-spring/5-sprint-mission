package com.sprint.mission.discodeit.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.UserAlreadyExistsException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class BaiscUserServiceTest {

  @Mock
  private UserRepository userRepository;
  @Mock
  private UserMapper userMapper;
  @InjectMocks
  private BasicUserService userService;

  private UserDto userDto;
  private User user;
  private String username;
  private String email;
  private String password;
  private UUID userId;

  @BeforeEach
  void setUp() {
    userId = UUID.randomUUID();
    username = "test01";
    email = "test01@email.com";
    password = "password1234";

    user = User.builder()
        .createdAt(Instant.now())
        .username(username)
        .email(email)
        .password(password)
        .profile(null)
        .build();
    userDto = new UserDto(userId, username, email, null, true);
  }

  @Test
  @DisplayName("사용자 생성 테스트 성공")
  void create_user() {
    UserCreateRequest req = new UserCreateRequest(
        username,
        email,
        password
    );

    given(userMapper.toDto(any())).willReturn(userDto);
    given(userRepository.existsByUsername(username)).willReturn(false);
    given(userRepository.save(any())).willReturn(user);

    UserDto result = userService.create(req, Optional.empty());

    Assertions.assertThat(result.username()).isEqualTo(username);
    verify(userRepository).save(any());
  }

  @Test
  @DisplayName("사용자 생성 실패")
  void create_user_fail() {
    UserCreateRequest request = new UserCreateRequest(username, email, password);
    given(userRepository.existsByEmail(email)).willReturn(true);

    Assertions.assertThatThrownBy(() -> userService.create(request, Optional.empty()))
        .isInstanceOf(UserAlreadyExistsException.class);
  }

  @Test
  @DisplayName("사용자 수정 성공")
  void update_user() {
    UserUpdateRequest req = new UserUpdateRequest("newUsername", "newEmail", "newPassword");
    given(userRepository.findById(any())).willReturn(Optional.of(user));
    given(userMapper.toDto(any())).willReturn(userDto);

    UserDto result = userService.update(userId, req, Optional.empty());
    Assertions.assertThat(result.username()).isEqualTo(username);
  }

  @Test
  @DisplayName("사용자 수정 실패")
  void update_user_fail() {
    UserUpdateRequest req = new UserUpdateRequest("asd", "asd@email.com", "asd");

    given(userRepository.findById(userId)).willReturn(Optional.empty());
    Assertions.assertThatThrownBy(() -> userService.update(userId, req, Optional.empty()))
        .isInstanceOf(UserNotFoundException.class);
  }


  //TODO 코드 리팩토링 필요
  @Test
  @DisplayName("사용자 삭제 테스트 성공")
  void delete_user() {
    given(userRepository.existsById(userId)).willReturn(true);

    userService.delete(userId);

    verify(userRepository).deleteById(userId);
  }

  @Test
  @DisplayName("사용자 삭제 테스트 실패")
  void delete_user_fail() {
    given(userRepository.existsById(userId)).willReturn(false);
    Assertions.assertThatThrownBy(() -> userService.delete(userId))
        .isInstanceOf(UserNotFoundException.class);
  }
}
