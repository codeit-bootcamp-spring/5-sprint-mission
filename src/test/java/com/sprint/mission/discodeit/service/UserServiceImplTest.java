package com.sprint.mission.discodeit.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.DuplicateUserException;
import com.sprint.mission.discodeit.exception.user.UserEmailAlreadyExistsException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.impl.UserServiceImpl;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/* 단위 테스트
 * 서비스 레이어의 주요 메소드에 대한 단위 테스트
 * DB, 외부 API 연결하지 않고 Mock을 사용해 검증
 */

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

  @Mock // 가짜 저장소
  private UserRepository userRepository;
  @Mock
  private UserMapper userMapper;
  @InjectMocks // 진짜 테스트 대상
  private UserServiceImpl userService;

  private UserDto userDto;
  private User user;

  @BeforeEach
    // 테스트 전 세팅
  void setUp() {
    userDto = new UserDto();
    userDto.setUsername("username");
    userDto.setPassword("password");
    userDto.setEmail("test@email.com");

    user = new User("username", "password", "test@email.com");
  }

  // --- create 성공 ---
  @Test
  void createUser_success() throws Exception {
    // given
    given(userRepository.existsByUsername("username")).willReturn(false);
    given(userRepository.existsByEmail("test@email.com")).willReturn(false);
    given(userMapper.toEntityForCreate(any(UserDto.class))).willReturn(user);
    given(userRepository.save(any(User.class))).willReturn(user);
    given(userMapper.toDto(any(User.class))).willReturn(userDto);

    // when
    UserDto result = userService.create(userDto, null);

    // then
    assertThat(result.getUsername()).isEqualTo("username");
    assertThat(result.getEmail()).isEqualTo("test@email.com");
  }

  // --- create 실패 (중복 username) ---
  @Test
  void createUser_fail_duplicateUsername() {
    // given
    given(userRepository.existsByUsername("username")).willReturn(true);

    // when & then
    assertThatThrownBy(() -> userService.create(userDto, null))
        .isInstanceOf(DuplicateUserException.class);
  }

  // --- create 실패 (중복 email) ---
  @Test
  void createUser_fail_duplicateEmail() {
    // given
    given(userRepository.existsByUsername("username")).willReturn(false);
    given(userRepository.existsByEmail("test@email.com")).willReturn(true);

    // when & then
    assertThatThrownBy(() -> userService.create(userDto, null))
        .isInstanceOf(UserEmailAlreadyExistsException.class);
  }

  // --- update 성공 ---
  @Test
  void updateUser_success() throws Exception {
    UUID id = UUID.randomUUID();
    UserDto updateDto = new UserDto();
    updateDto.setUsername("username");
    updateDto.setNewPassword("newpassword");
    updateDto.setEmail("test@email.com");
    updateDto.setPassword("newpassword"); // 추가!

    User userFromDb = new User("username", "password", "test@email.com");

    given(userRepository.findById(id)).willReturn(Optional.of(userFromDb));
    given(userMapper.toDto(any(User.class))).willReturn(updateDto);

    // when
    UserDto result = userService.update(id, updateDto, null);

    // then
    assertThat(result.getUsername()).isEqualTo("username");
    assertThat(result.getPassword()).isEqualTo("newpassword");
  }


  // --- update 실패 ---
  @Test
  void updateUser_fail_notFound() {
    UUID id = UUID.randomUUID();
    given(userRepository.findById(id)).willReturn(Optional.empty());

    assertThatThrownBy(() -> userService.update(id, userDto, null))
        .isInstanceOf(UserNotFoundException.class);
  }

  // --- delete 성공 ---
  @Test
  void deleteUser_success() {
    UUID id = UUID.randomUUID();
    given(userRepository.findById(id)).willReturn(Optional.of(user));
    willDoNothing().given(userRepository).delete(user);

    userService.delete(id); // 예외 없으면 성공
  }

  // --- delete 실패 ---
  @Test
  void deleteUser_fail_notFound() {
    UUID id = UUID.randomUUID();
    given(userRepository.findById(id)).willReturn(Optional.empty());

    assertThatThrownBy(() -> userService.delete(id))
        .isInstanceOf(UserNotFoundException.class);
  }
}
